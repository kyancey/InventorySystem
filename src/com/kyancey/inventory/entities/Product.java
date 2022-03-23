package com.kyancey.inventory.entities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * <p>The product class stores the basic properties of a product.</p>
 * FUTURE ENHANCEMENT: Data validation.
 * FUTURE ENHANCEMENT: Method to find a list of parts that are no
 * longer in inventory.
 */
public class Product {
    private ObservableList<Part> associatedParts;
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;

    /**
     * <p>Constructor</p>
     * @param id Product Id.
     * @param name Product name.
     * @param price Product price.
     * @param stock How much of product in stock.
     * @param min Minimum number of product that must be in inventory.
     * @param max Maximum number of product that can be in inventory.
     */
    public Product(int id, String name, double price, int stock, int min, int max) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
        this.associatedParts = FXCollections.observableArrayList(new ArrayList<>());
    }

    /**
     * <p>Gets the product Id.</p>
     * @return Product Id.
     */
    public int getId() {
        return id;
    }

    /**
     * <p>Sets the product Id.</p>
     * @param id Product Id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * <p>Gets the product name.</p>
     * @return Product name.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Sets the product name.</p>
     * @param name Product name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Gets the product price.</p>
     * @return Product price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * <p>Sets the product price.</p>
     * @param price Product price.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * <p>Gets how many of the product are in stock.</p>
     * @return Number of product in stock.
     */
    public int getStock() {
        return stock;
    }

    /**
     * <p>Sets how many of the product are in stock.</p>
     * @param stock Number of product in stock.
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * <p>Gets the minimum number of product that must be in stock.</p>
     * @return Minimum number of product.
     */
    public int getMin() {
        return min;
    }

    /**
     * <p>Sets the minimum number of product that must be in stock.</p>
     * @param min Minimum number of product.
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * <p>Gets the maximum number of product that can be in stock.</p>
     * @return Maximum number of product.
     */
    public int getMax() {
        return max;
    }

    /**
     * <p>Sets the maximum number of product that can be in stock.</p>
     * @param max Maximum number or product
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * <p>Associates a part with the product.</p>
     * @param part Part to be added.
     */
    public void addAssociatedPart(Part part) {
        associatedParts.add(part);
    }

    /**
     * <p>Deletes a part associated with the product.</p>
     * @param selectedAssociatedPart Part to be deleted.
     * @return True if part existed. False if part was not found.
     */
    public boolean deleteAssociatedPart(Part selectedAssociatedPart) {
        for (int i = 0; i < associatedParts.size(); i++) {
            if(associatedParts.get(i).getId() == selectedAssociatedPart.getId()) {
                associatedParts.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * <p>Gets a list of associated parts.</p>
     * @return ObservableList of associated parts.
     */
    public ObservableList<Part> getAllAssociatedParts() {
        return FXCollections.observableArrayList(this.associatedParts);
    }

    /**
     * <p>Clears entire list of associated parts.</p>
     */
    public void clearAssociatedParts() {
        this.associatedParts.clear();
    }
}
