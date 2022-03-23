package com.kyancey.inventory.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import com.kyancey.inventory.entities.FormMode;
import com.kyancey.inventory.entities.Inventory;
import com.kyancey.inventory.entities.Part;
import com.kyancey.inventory.entities.Product;
import com.kyancey.inventory.entities.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


/**
 * <p>Controller for the Main Form Window</p>
 * FUTURE ENHANCEMENT: When a part is selected, highlight all of the products that contain
 * that part in the products table. Inversely, when a product is selected, highlight all the
 * parts associated with that product in the parts table.
 */
public class MainController {
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;

    private Stage primaryStage;
    private ObservableList<Part> parts;
    private ObservableList<Product> products;

    // Buttons
    @FXML
    public Button addPartButton;
    @FXML
    public Button modifyPartButton;
    @FXML
    public Button deletePartButton;
    @FXML
    public Button addProductButton;
    @FXML
    public Button modifyProductButton;
    @FXML
    public Button deleteProductButton;
    @FXML
    private Button closeButton;

    // Tables
    @FXML
    public TableView<Part> partTable;
    @FXML
    private TableColumn<Part, Integer> partID;
    @FXML
    private TableColumn<Part, String> partName;
    @FXML
    private TableColumn<Part, Integer> partInventoryLevel;
    @FXML
    private TableColumn<Part, String> partPrice;
    @FXML
    public TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> productID;
    @FXML
    private TableColumn<Product, String > productName;
    @FXML
    private TableColumn<Product, Integer> productInventoryLevel;
    @FXML
    private TableColumn<Product, String> productPrice;

    // Fields
    @FXML
    public TextField productSearchField;
    @FXML
    public TextField partSearchField;

    /**
     * Sets up our windows on startup.
     */
    @FXML
    void initialize() {
        // Set columns up for part table
        partID.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        partName.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getName()));
        partInventoryLevel.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStock()));
        partPrice.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(String.format("%.2f", cellData.getValue().getPrice())));

        // Set columns up for product table
        productID.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        productName.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getName()));
        productInventoryLevel.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStock()));
        productPrice.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(String.format("%.2f", cellData.getValue().getPrice())));

        // Set up filtered lists
        parts = Inventory.getAllParts();
        products = Inventory.getAllProducts();
        FilteredList<Part> filteredParts = new FilteredList<>(parts);
        FilteredList<Product> filteredProducts = new FilteredList<>(products);

        // Set items and event listener callback to change predicate
        partTable.setItems(filteredParts);
        productTable.setItems(filteredProducts);
        partSearchField.textProperty().addListener((observable, oldValue, newValue) -> filteredParts.setPredicate(createPartPredicate(newValue)));
        productSearchField.textProperty().addListener((observable, oldValue, newValue) -> filteredProducts.setPredicate(createProductPredicate(newValue)));
    }

    /**
     * <p>Creates a product predicate for the given search text.</p>
     * @param text Search text
     * @return Product predicate
     */
    private Predicate<Product> createProductPredicate(String text) {
        return product -> {
            if (text == null || text.isEmpty()) { return true; }
            return searchFindsProduct(product, text);
        };
    }

    /**
     * <p>Creates a part predicate for the given search text.</p>
     * @param text Search text
     * @return Part predicate
     */
    private Predicate<Part> createPartPredicate(String text) {
        return part -> {
            if (text == null || text.isEmpty()) {
                return true;
            }
            return searchFindsPart(part, text);
        };
    }

    /**
     * <p>Compares the given product to the search text to identify a match.</p>
     * @param part Part to be compared
     * @param text Search text
     * @return True if text matches part id or name. False otherwise.
     */
    private boolean searchFindsPart(Part part, String text) {
        boolean foundName = part.getName().toLowerCase().contains(text.toLowerCase());
        boolean foundPartID = Integer.toString(part.getId()).equals(text.toLowerCase());
        return  foundName || foundPartID;
    }

    /**
     * <p>compares the given product to the search text to identify a match.</p>
     * @param product Product to be compared
     * @param text Search text
     * @return True if text matches product id or name. False otherwise.
     */
    private boolean searchFindsProduct(Product product, String text) {
        boolean foundName = product.getName().toLowerCase().contains(text.toLowerCase());
        boolean foundProductID = Integer.toString(product.getId()).equals(text.toLowerCase());
        return foundName || foundProductID;
    }

    /**
     * Stores the primary stage in a class member.
     * @param primaryStage Primary Stage
     */
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * <p>Closes app when button is clicked.</p>
     * @param action Provided by event system
     */
    @FXML
    private void onButtonClose(ActionEvent action) {
        primaryStage.close();
    }

    /**
     * <p>Transitions to Part form in add mode.</p>
     * @param action Provided by event system.
     * @throws IOException
     */
    @FXML
    private void onAddPartButton(ActionEvent action) throws IOException {
        // Load the fxml file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../fxml/part.fxml"));
        Parent root = loader.load();

        // Set the scene and pass inventory to the controller
        Scene scene = new Scene(root);
        PartController controller = loader.getController();
        controller.setStage(primaryStage);
        controller.setMode(FormMode.ADD);

        // Set the window up
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * <p>Transitions to Part form in modify mode. Passes on specific part.</p>
     * @param action Provided by event system.
     * @throws IOException
     */
    @FXML
    private void onModifyPartButton(ActionEvent action) throws IOException {
        if (partTable.getSelectionModel().getSelectedItem() == null) return;

        // Load the fxml file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../fxml/part.fxml"));
        Parent root = loader.load();

        // Set the scene and pass inventory to the controller
        Scene scene = new Scene(root);
        PartController controller = loader.getController();
        controller.setStage(primaryStage);
        controller.setPart(partTable.getSelectionModel().getSelectedItem());
        controller.setMode(FormMode.MODIFY);

        // Set the window up
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * <p>Transitions to the Product form in add mode.</p>
     * @param action Provided by event system.
     * @throws IOException
     */
    @FXML
    private void onAddProductButton(ActionEvent action) throws IOException {
        // Load the fxml file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../fxml/product.fxml"));
        Parent root = loader.load();

        // Set the scene and pass inventory to the controller
        Scene scene = new Scene(root);
        ProductController controller = loader.getController();
        controller.setStage(primaryStage);
        controller.setMode(FormMode.ADD);

        // Set the window up
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * <p>Transitions to Product form in modify mode. Passes on specific product.</p>
     * @param action Provided by event system.
     * @throws IOException
     */
    @FXML
    private void onModifyProductButton(ActionEvent action) throws IOException {
        if (productTable.getSelectionModel().getSelectedItem() == null) return;

        // Load the fxml file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../fxml/product.fxml"));
        Parent root = loader.load();

        // Set the scene and pass inventory to the controller
        Scene scene = new Scene(root);
        ProductController controller = loader.getController();
        controller.setStage(primaryStage);
        controller.setProduct(productTable.getSelectionModel().getSelectedItem());
        controller.setMode(FormMode.MODIFY);

        // Set the window up
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * <p>Deletes selected product.</p>
     * RUNTIME ERROR: Added check to make sure something was selected.
     * Otherwise, an error was raised about referencing a null pointer.
     * @param action Provided by event system.
     */
    @FXML
    private void onDeleteProductButton(ActionEvent action) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) return;

        if (selectedProduct.getAllAssociatedParts().size() == 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the product?");
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
                Inventory.deleteProduct(productTable.getSelectionModel().getSelectedItem());
                products.remove(productTable.getSelectionModel().getSelectedItem());
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Can't delete product because there are still parts" +
                    " associated with it.");
            alert.showAndWait();
        }
    }

    /**
     * <p>Deletes selected Part.</p>
     * @param action Provided by event system.
     */
    @FXML
    private void onDeletePartButton(ActionEvent action) {
        Part selectedPart = partTable.getSelectionModel().getSelectedItem();

        if (selectedPart == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the part?");
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            Inventory.deletePart(partTable.getSelectionModel().getSelectedItem());
            parts.remove(partTable.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * <p>Transitions to the Part form in modify mode when an item is double clicked.</p>
     * @param mouseEvent Provided by event system.
     * @throws IOException
     */
    @FXML
    public void onPartTableMouseClick(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() == 2) {
            onModifyPartButton(new ActionEvent());
        }
    }

    /**
     * <p>Transitions to the Product form in modify mode when an item is double clicked.</p>
     * @param mouseEvent Provided by event system.
     * @throws IOException
     */
    @FXML
    public void onProductTableMouseClick(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() == 2) {
            onModifyProductButton(new ActionEvent());
        }
    }
}
