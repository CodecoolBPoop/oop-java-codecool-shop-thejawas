package com.codecool.shop.util.json_file_handling;

import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonMappingHandler {

    static void listToJsonFile(String filePath, List list) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            objectMapper.writeValue(new File(filePath), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Supplier> jsonFileToSupplierList(String fileName) {

        ObjectMapper objectMapper = new ObjectMapper();

        List<Supplier> objectList = null;
        try {
            objectList = objectMapper.readValue(new File(fileName), new TypeReference<List<Supplier>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return objectList;
    }

    public static List<ProductCategory> jsonFileToProductCategoryList(String fileName) {

        ObjectMapper objectMapper = new ObjectMapper();

        List<ProductCategory> objectList = null;
        try {
            objectList = objectMapper.readValue(new File(fileName), new TypeReference<List<ProductCategory>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return objectList;
    }

    public static List<Product> jsonFileToProductList(String fileName) {

        ObjectMapper objectMapper = new ObjectMapper();

        List<Product> objectList = null;
        try {
            objectList = objectMapper.readValue(new File(fileName), new TypeReference<List<Product>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return objectList;
    }

    public static List<?> jsonFileToObjectList(String fileName, String fullyQualifiedName) throws ClassNotFoundException {

        Class<?> aClass = Class.forName(fullyQualifiedName);
        ObjectMapper objectMapper = new ObjectMapper();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, aClass);

        List<?> objectList = Collections.singletonList(listType);

        try {
            objectList = objectMapper.readValue(new File(fileName), listType);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return objectList;
    }


}
