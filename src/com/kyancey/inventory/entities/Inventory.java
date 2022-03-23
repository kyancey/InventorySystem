package com.kyancey.inventory.entities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

/**
 * <p>The inventory class stores the inventory of parts and products.</p>
 * FUTURE ENHANCEMENT: Might want to consider doing something about referential
 * integrity in the future. While it makes sense that products may still exist
 * that were created from parts that no longer exist, it would make more sense
 * to prevent the parts from being deleted until all of the derivative products
 * are gone. The parts min, max, and inventory could simply be set to 0.
 */
public class Inventory {
    private static ObservableList<Part> allParts = FXCollections.observableArrayList(new ArrayList<>());
    private static ObservableList<Product> allProducts = FXCollections.observableArrayList(new ArrayList<>());

    /**
     * <p>Adds a part to the inventory.</p>
     * @param newPart Part to be added to the inventory.
     */
    public static void addPart(Part newPart) {
        allParts.add(newPart);
    }

    /**
     * <p>Adds a product to the inventory.</p>
     * @param newProduct Product to be added to the inventory.
     */
    public static void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }

    /**
     * <p>Looks up a single part with a unique part id.</p>
     * <p><em>Assumption</em>: No two parts will ever have the same id.</p>
     * @param partId The id of the part to be found.
     * @return Part with matching id or null if no part matches.
     */
    public static Part lookupPart(int partId) {
        Part result = null;
        for (Part p : allParts) {
            if (p.getId() == partId) {
                result = p;
            }
        }
        return result;
    }

    /**
     * <p>Looks up a single product with a unique product id.</p>
     * <p><em>Assumption</em>: No two products will ever have the same id.</p>
     * @param productId The id of the product to be found.
     * @return Product with matching id or null if no product matches.
     */
    public static Product lookupProduct(int productId) {
        Product result = null;
        for (Product p: allProducts) {
            if (p.getId() == productId) {
                result = p;
            }
        }
        return result;
    }

    /**
     * <p>Looks up all parts with the name provided.</p>
     * <p><em>Assumption:</em> Names are case sensitive.</p>
     * @param partName Name of part as String.
     * @return ObservableList of matching parts.
     */
    public static ObservableList<Part> lookupPart(String partName) {
        ObservableList<Part> result = FXCollections.observableArrayList(new ArrayList<>());
        for (Part p: allParts) {
            if (p.getName().equals(partName)) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * <p>Looks up all products with the name provided.</p>
     * <p><em>Assumption:</em> Names are case sensitive.</p>
     * @param productName Name of product as String.
     * @return ObservableList of matching products.
     */
    public static ObservableList<Product> lookupProduct(String productName) {
        ObservableList<Product> result = FXCollections.observableArrayList(new ArrayList<>());
        for (Product p: allProducts) {
            if (p.getName().equals(productName)) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * <p>Updates the data for a part in inventory.</p>
     * <p><em>Assumptions</em>: The ObservableList in Inventory is synchronized with the
     * ObservableList in the GUI so that indices match the same referenced part.</p>
     * @param index Index of part
     * @param selectedPart Part object with updated data.
     */
    public static void updatePart(int index, Part selectedPart) {
        Part p = allParts.get(index);
        p.setId(selectedPart.getId());
        p.setName(selectedPart.getName());
        p.setStock(selectedPart.getStock());
        p.setPrice(selectedPart.getPrice());
        p.setMin(selectedPart.getMin());
        p.setMax(selectedPart.getMax());
    }

    /**
     * <p>Updates the data for a product in inventory.</p>
     * <p><em>Assumption</em>: The ObservableList in Inventory is synchronized with the
     * ObservableList in the GUI so that indices match the same referenced product.</p>
     * @param index Index of product
     * @param newProduct Product object with updated data
     */
    public static void updateProduct(int index, Product newProduct) {
        Product p = allProducts.get(index);
        p.setId(newProduct.getId());
        p.setName(newProduct.getName());
        p.setStock(newProduct.getStock());
        p.setPrice(newProduct.getPrice());
        p.setMin(newProduct.getMin());
        p.setMax(newProduct.getMax());
    }

    /**
     * <p>Deletes part from inventory.</p>
     * @param selectedPart Part to be deleted.
     * @return True if part existed. False if part was not found.
     */
    public static boolean deletePart(Part selectedPart) {
        for (int i = 0; i < allParts.size(); i++) {
            if(allParts.get(i).getId() == selectedPart.getId()) {
                allParts.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * <p>Deletes product from inventory.</p>
     * @param selectedProduct Product to be deleted.
     * @return True if product existed. False if product was not found.
     */
    public static boolean deleteProduct(Product selectedProduct) {
        for (int i = 0; i < allProducts.size(); i++) {
            if(allProducts.get(i).getId() == selectedProduct.getId()) {
                allProducts.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * <p>Get a list of all the parts in inventory.</p>
     * @return ObservableList of all parts in inventory.
     */
    public static ObservableList<Part> getAllParts() {
        return FXCollections.observableArrayList(allParts);
    }

    /** <p>Get a list of all the products in inventory.</p>
     * @return ObservableList of all products in inventory.
     */
    public static ObservableList<Product> getAllProducts() {
        return FXCollections.observableArrayList(allProducts);
    }
}
