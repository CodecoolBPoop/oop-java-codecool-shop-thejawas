package com.codecool.shop.config;

import com.codecool.shop.dao.*;
import com.codecool.shop.dao.implementation.Memory.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.Memory.ProductDaoMem;
import com.codecool.shop.dao.implementation.Memory.SupplierDaoMem;
import com.codecool.shop.model.*;
import com.codecool.shop.util.CartStatusType;
import com.codecool.shop.util.ConfigurationHandler;
import com.codecool.shop.util.ImplementationType;
import com.codecool.shop.util.implementation_factory.DatabaseFactory;
import com.codecool.shop.util.implementation_factory.ImplementationFactory;
import com.codecool.shop.util.implementation_factory.MemoryFactory;
import com.codecool.shop.util.json_file_handling.JsonMappingHandler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.EnumMap;
import java.util.List;

@WebListener
public class Initializer implements ServletContextListener {

    private static final String CONFIGURATION_PROPERTY_NAME = "implementation";

    private static final ImplementationType CURRENT_IMPLEMENTATION;
    private static final EnumMap<ImplementationType, ImplementationFactory> implementationFactoryEnumMap = new EnumMap<>(ImplementationType.class);

    private static final ImplementationFactory IMPLEMENTATION_FACTORY;

    static {
        implementationFactoryEnumMap.put(ImplementationType.MEMORY, new MemoryFactory());
        implementationFactoryEnumMap.put(ImplementationType.DATABASE, new DatabaseFactory());

        String currentConfiguration = ConfigurationHandler.readConfigurationProperty(CONFIGURATION_PROPERTY_NAME);
        CURRENT_IMPLEMENTATION = ImplementationType.valueOf(currentConfiguration);

        try {
            IMPLEMENTATION_FACTORY = implementationFactoryEnumMap.get(CURRENT_IMPLEMENTATION);
        } catch (NullPointerException npe) {
            System.out.println("No such implementation: " + currentConfiguration);
            npe.printStackTrace();
            throw new NullPointerException(npe.getMessage());
        }

    }

    public static ImplementationFactory getImplementationFactory() {
        return IMPLEMENTATION_FACTORY;
    }


    private final CartDao CART_DATA_MANAGER = IMPLEMENTATION_FACTORY.getCartDataManagerInstance();
    private final CustomerDao CUSTOMER_DATA_MANAGER = IMPLEMENTATION_FACTORY.getCustomerDataManagerInstance();


    @Override
    public void contextInitialized(ServletContextEvent sce) {

        if (CURRENT_IMPLEMENTATION == ImplementationType.MEMORY) {
            try {
                initializeFromMemory();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (CURRENT_IMPLEMENTATION == ImplementationType.DATABASE) {
            initializeFromDatabase();
        }
        

    }

    private void initializeFromDatabase() {
        deleteEmptyCarts();

        addGuestToNewCartBy(CUSTOMER_DATA_MANAGER.getGuestId());
    }

    private void deleteEmptyCarts() {
        CART_DATA_MANAGER.remove(CartStatusType.EMPTY);
    }

    private void initializeFromMemory() throws ClassNotFoundException {

        String filePathForSupplierData = "src/main/resources/json_data_persistence/supplier_data.json";
        loadSuppliersIntoMemory(filePathForSupplierData);

        String filePathForProductCategoryData = "src/main/resources/json_data_persistence/product_category_data.json";
        loadProductCategoriesIntoMemory(filePathForProductCategoryData);

        String filePathForProductData = "src/main/resources/json_data_persistence/product_data.json";
        loadProductsIntoMemory(filePathForProductData);

        addGuestToNewCartBy(createGuestUser());

    }

    private void addGuestToNewCartBy(int guestId) {
        CART_DATA_MANAGER.add(new Cart(CART_DATA_MANAGER.generateIdForNewCart(), guestId, CartStatusType.EMPTY));
    }

    private int createGuestUser() {
        Customer guest = new Customer(CUSTOMER_DATA_MANAGER.generateIdForNewCustomer(), "Guest");
        CUSTOMER_DATA_MANAGER.add(guest);
        return guest.getId();
    }

    private void loadSuppliersIntoMemory(String filePath) throws ClassNotFoundException {
        List<Supplier> supplierList = null;
        try {
            supplierList = (List<Supplier>) JsonMappingHandler.jsonFileToObjectList(filePath, "com.codecool.shop.model.Supplier");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("\"Supplier\" class is not found when initializing from json file");
            throw new ClassNotFoundException(cnfe.getMessage());
        }

        SupplierDaoMem supplierDataManager = SupplierDaoMem.getInstance();
        supplierDataManager.setData(supplierList);
    }

    private void loadProductCategoriesIntoMemory(String filePath) throws ClassNotFoundException {
        ProductCategoryDaoMem productCategoryDataManager = ProductCategoryDaoMem.getInstance();
        List<ProductCategory> productCategoryList = null;
        try {
            productCategoryList = (List<ProductCategory>) JsonMappingHandler.jsonFileToObjectList(filePath, "com.codecool.shop.model.ProductCategory");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("ProductCategory class is not found when initializing from json file");
            throw new ClassNotFoundException(cnfe.getMessage());
        }

        productCategoryDataManager.setData(productCategoryList);
    }

    private void loadProductsIntoMemory(String filePath) throws ClassNotFoundException {
        ProductDaoMem productDataManager = ProductDaoMem.getInstance();
        List<Product> productList = null;
        try {
            productList = (List<Product>) JsonMappingHandler.jsonFileToObjectList(filePath, "com.codecool.shop.model.Product");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Product class is not found when initializing from json file");
            throw new ClassNotFoundException(cnfe.getMessage());
        }

        productDataManager.setData(productList);
    }


}