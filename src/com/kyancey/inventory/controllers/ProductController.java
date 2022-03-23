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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller for the Product form.
 * FUTURE ENHANCEMENT: Highlight parts that are associated with the
 * product but no longer in inventory system.
 * FUTURE ENHANCEMENT: Change inventory field to slider and bound it
 * to min and max. Change min and max to have increment and decrement arrows.
 */
public class ProductController {
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;

    private Stage primaryStage;
    private Product product;
    private FormMode mode;
    private ObservableList<Part> associatedParts;

    // Fields
    @FXML
    private TextField partSearchField;
    @FXML
    private TextField productIDField;
    @FXML
    private TextField productNameField;
    @FXML
    private TextField productInventoryField;
    @FXML
    private TextField productPriceField;
    @FXML
    private TextField productMaxField;
    @FXML
    private TextField productMinField;

    // Tables
    @FXML
    private TableView<Part> partTable;
    @FXML
    private TableView<Part> associatedPartTable;
    @FXML
    public TableColumn<Part, Integer> partID;
    @FXML
    public TableColumn<Part, String> partName;
    @FXML
    public TableColumn<Part, Integer> partInventoryLevel;
    @FXML
    public TableColumn<Part, String> partPrice;
    @FXML
    public TableColumn<Part, Integer> associatedPartID;
    @FXML
    public TableColumn<Part, String> associatedPartName;
    @FXML
    public TableColumn<Part, Integer> associatedPartInventoryLevel;
    @FXML
    public TableColumn<Part, String> associatedPartPrice;

    // Buttons
    @FXML
    private Button removePartButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addButton;

    /**
     * <p>Empty because initialization is happening after the mode is passed to the controller.</p>
     */
    @FXML
    void initialize() {
    }

    // Setters for info the controller needs

    /**
     * <p>Stores the primary stage in a class member.</p>
     * @param primaryStage Primary stage
     */
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * <p>Initializes the form in either add or modify mode.</p>
     * RUNTIME ERROR: No longer need to check for malicious form
     * data now that it's being validated.
     * @param mode Form mode
     */
    public void setMode(FormMode mode) {
        this.mode = mode;

        // Set columns up for part table
        partID.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        partName.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getName()));
        partInventoryLevel.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStock()));
        partPrice.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(String.format("%.2f", cellData.getValue().getPrice())));

        // Set up columns for the associated part table
        associatedPartID.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        associatedPartName.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getName()));
        associatedPartInventoryLevel.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStock()));
        associatedPartPrice.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(String.format("%.2f", cellData.getValue().getPrice())));

        // Set up filtered lists
        FilteredList<Part> filteredParts = new FilteredList<>(Inventory.getAllParts());
        partSearchField.textProperty().addListener((observable, oldValue, newValue) -> filteredParts.setPredicate(createPartPredicate(newValue)));

        // Set part inventory table.
        partTable.setItems(filteredParts);

        if (mode == FormMode.ADD) {
            // This will all be overwritten, but we need a dummy object in place.
            this.product = new Product(generateID(), "", 0.00,3,1,100);

            productNameField.setText("");
            productInventoryField.setText("");
            productPriceField.setText("");
            productMaxField.setText("");
            productMinField.setText("");

            // Set up filtered list of associated parts
            // This will be blank initially
            associatedParts = product.getAllAssociatedParts();
            FilteredList<Part> filteredAssociatedParts = new FilteredList<>(associatedParts);

            // Set associated part inventory table.
            associatedPartTable.setItems(filteredAssociatedParts);
        } else if (mode == FormMode.MODIFY) {
            productIDField.setText(Integer.toString(product.getId()));
            productNameField.setText(product.getName());
            productInventoryField.setText(Integer.toString(product.getStock()));
            productPriceField.setText(Double.toString(product.getPrice()));
            productMaxField.setText(Integer.toString(product.getMax()));
            productMinField.setText(Integer.toString(product.getMin()));

            // Set up filtered list of associated parts
            associatedParts = product.getAllAssociatedParts();
            FilteredList<Part> filteredAssociatedParts = new FilteredList<>(associatedParts);

            // Set associated part inventory table.
            associatedPartTable.setItems(filteredAssociatedParts);
        }
    }

    /**
     * <p>Creates a part predicate for a particular search text.</p>
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
     * <p>Compares the given part to the search text to identify a match.</p>
     * @param part Part to be compared.
     * @param text Search text.
     * @return True if text matches part id or name. False otherwise.
     */
    private boolean searchFindsPart(Part part, String text) {
        boolean foundName = part.getName().toLowerCase().contains(text.toLowerCase());
        boolean foundPartID = Integer.toString(part.getId()).equals(text.toLowerCase());
        return  foundName || foundPartID;
    }

    /**
     *<p>Generates an ID by scanning through the current inventory of products to find
     * the first available number in sequential order.</p>
     * @return Numeric ID
     */
    private int generateID() {
        int i = 0;
        for (; i < Integer.MAX_VALUE; i++) {
            if (Inventory.lookupProduct(i) == null) {
                break;
            }
        }
        return i;
    }

    /**
     * <p>Stores the product to be modified in a class member.</p>
     * @param product Product to be modified.
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    // Event handlers

    /**
     * <p>Removes a part association between a part and a product.</p>
     * Note: Does not remove a part from inventory.
     * @param actionEvent Provided by event system.
     */
    @FXML
    public void onRemoveAssociatedPartButton(ActionEvent actionEvent) {
        Part selectedPart = associatedPartTable.getSelectionModel().getSelectedItem();
        if (selectedPart == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to dissociate this part?");
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            associatedParts.remove(selectedPart);
        }
    }

    /**
     * <p>Stores form information in a Product instance. If in add mode, adds
     * the instance to inventory. If in modify mode, alters the information in
     * place.</p>
     * @param actionEvent Provided by event system.
     * @throws IOException
     */
    @FXML
    public void onSaveButton(ActionEvent actionEvent) throws IOException {
        int stock = 0, min = 0, max = 0;
        double price = 0;
        try {
            stock = Integer.parseInt(productInventoryField.getText());
            price = Double.parseDouble(productPriceField.getText());
            max = Integer.parseInt(productMaxField.getText());
            min = Integer.parseInt(productMinField.getText());
        }
        catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Only numeric data allowed in numeric fields.");
            alert.showAndWait();
            return;
        }

        // Validate Stock Data
        if (min > max) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Min cannot be greater than max.");
            alert.showAndWait();
            return;
        }
        if (stock < min) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Inventory cannot be less than min.");
            alert.showAndWait();
            return;
        }
        if (stock > max) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Inventory cannot be greater than max.");
            alert.showAndWait();
            return;
        }

        product.setName(productNameField.getText());
        product.setStock(stock);
        product.setPrice(price);
        product.setMax(max);
        product.setMin(min);

        // Copy list from GUI to data model
        product.clearAssociatedParts();
        for (Part p : associatedPartTable.getItems()) {
            product.addAssociatedPart(p);
        }

        if (mode == FormMode.ADD) {
            Inventory.addProduct(product);
        } else if (mode == FormMode.MODIFY) {
        }

        navigateToMainForm();
    }

    /**
     * <p>Navigates back to Main form and discards any work.</p>
     * @param actionEvent Provided by event system.
     * @throws IOException
     */
    @FXML
    public void onCancelButton(ActionEvent actionEvent) throws IOException {
        navigateToMainForm();
    }

    /**
     * <p>Helper function that navigates back to Main form.</p>
     * @throws IOException
     */
    private void navigateToMainForm() throws IOException {
        // Load the fxml file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../fxml/main.fxml"));
        Parent root = loader.load();

        // Set the scene and pass inventory to the controller
        Scene scene = new Scene(root);
        MainController controller = loader.getController();
        controller.setStage(primaryStage);

        // Set the window up
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * <p>Adds an association between the selected part and the
     * product when the add button is pressed.</p>
     * @param actionEvent Provided by event system.
     */
    @FXML
    public void onAddButton(ActionEvent actionEvent) {
        if (partTable.getSelectionModel().getSelectedItem() == null) return;

        associatedParts.add(partTable.getSelectionModel().getSelectedItem());
    }

    /**
     * <p>Adds an association between the selected part and the
     * product when the part is double clicked.</p>
     * @param mouseEvent Provided by event system.
     */
    @FXML
    public void onPartTableMouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            onAddButton(new ActionEvent());
        }
    }

    /**
     * <p>Removes an association between the selected part and the
     * product when the part is double clicked.</p>
     * @param mouseEvent Provided by event system.
     */
    public void onAssociatedTableMouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            onRemoveAssociatedPartButton(new ActionEvent());
        }
    }
}
