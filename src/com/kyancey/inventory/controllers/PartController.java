package com.kyancey.inventory.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.kyancey.inventory.entities.*;
import com.kyancey.inventory.entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for the Part form.
 * FUTURE ENHANCEMENT: Currently, the form erases company name when switching to In-House.
 * We should store that information in case the user switches back to Outsourced.
 */
public class PartController {
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;

    private Stage primaryStage;
    private Part part;
    private FormMode mode;

    // Labels
    @FXML
    private Label partSpecialLabel;

    // Radio Buttons
    @FXML
    private RadioButton partInHouseRadio;
    @FXML
    private RadioButton partOutsourcedRadio;
    @FXML
    private ToggleGroup partToggle;

    // Text Fields
    @FXML
    private TextField partIDField;
    @FXML
    private TextField partNameField;
    @FXML
    private TextField partInventoryField;
    @FXML
    private TextField partPriceField;
    @FXML
    private TextField partMaxField;
    @FXML
    private TextField partMinField;
    @FXML
    private TextField partSpecialField;

    // Buttons
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    /**
     * <p>Empty because all initialization must happen after the mode information is passed to the controller.</p>
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
     * <p>Stores the part to be modified in a class member. If in add mode, this will be discarded.
     * If in modify mode, this must be set or a null pointer exception will result.</p>
     * @param part Part to be modified.
     */
    public void setPart(Part part) {
        this.part = part;
    }

    /**
     * <p>Initializes the form in either Add or Modify mode.</p>
     * @param mode Form mode
     */
    public void setMode(FormMode mode) {
        this.mode = mode;
        if(mode == FormMode.ADD) {
            partNameField.setText("");
            partInventoryField.setText("");
            partPriceField.setText("");
            partMinField.setText("");
            partMaxField.setText("");
            partSpecialField.setText("");
            partSpecialLabel.setText("Machine ID");
            partInHouseRadio.setSelected(true);
        } else if (mode == FormMode.MODIFY) {
            partIDField.setText(Integer.toString(part.getId()));
            partNameField.setText(part.getName());
            partInventoryField.setText(String.format("%d", part.getStock()));
            partPriceField.setText(String.format("%.2f", part.getPrice()));
            partMinField.setText(String.format("%d", part.getMin()));
            partMaxField.setText(String.format("%d", part.getMax()));

            if (part instanceof InHouse) {
                int id = ((InHouse) part).getMachineId();
                String machineID = Integer.toString(id);
                partSpecialField.setText(machineID);
                partInHouseRadio.setSelected(true);
                partSpecialLabel.setText("Machine ID");
            } else if (part instanceof Outsourced) {
                String companyName = ((Outsourced) part).getCompanyName();
                partSpecialField.setText(companyName);
                partOutsourcedRadio.setSelected(true);
                partSpecialLabel.setText("Company Name");
            }
        }
    }

    /**
     * <p>Generates an ID by scanning through the current inventory of parts to find
     * the first available number in sequential order.</p>
     * @return Numeric ID
     */
    private int generateID() {
        int i = 0;
        for (; i < Integer.MAX_VALUE; i++) {
            if (Inventory.lookupPart(i) == null) {
                break;
            }
        }
        return i;
    }

    // Event Handlers

    /**
     * <p>Saves the current work either in a new Part or the existing Part. The part
     * is then saved back to the inventory.</p>
     * RUNTIME ERROR: Since data is being validated through our event handlers,
     * it is not being validated a second time here. It is assumed to be correct now.
     * @param action Provided by event system.
     * @throws IOException
     */
    @FXML
    private void onSaveButton(ActionEvent action) throws IOException {
        double price = 0;
        int stock = 0, min = 0, max = 0;
        String name = partNameField.getText();
        try {
            price = Double.parseDouble(partPriceField.getText());
            stock = Integer.parseInt(partInventoryField.getText());
            min = Integer.parseInt(partMinField.getText());
            max = Integer.parseInt(partMaxField.getText());
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

        if (mode == FormMode.ADD) {
            // Get all the values ready
            Part partToAdd = null;
            int id = generateID();

            if (partOutsourcedRadio.isSelected()) {
                String companyName = partSpecialField.getText();
                partToAdd = new Outsourced(id, name, price, stock, min, max, companyName);
            } else if (partInHouseRadio.isSelected()) {
                // Get the machine id and make sure it's valid
                int machineID = 0;
                try {
                    machineID = Integer.parseInt(partSpecialField.getText());
                }
                catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Only numeric data allowed in numeric fields.");
                    alert.showAndWait();
                    return;
                }
                partToAdd = new InHouse(id, name, price, stock, min, max, machineID);
            }
            Inventory.addPart(partToAdd);
        } else if (mode == FormMode.MODIFY) {
            Part part = null;

            if (partOutsourcedRadio.isSelected()) {
                part = new Outsourced(this.part.getId(),
                                      name,
                                      price,
                                      stock,
                                      min,
                                      max,
                                      partSpecialField.getText()
                        );
            } else if (partInHouseRadio.isSelected()) {
                // Get the machine id and make sure it's valid
                int machineID = 0;
                try {
                    machineID = Integer.parseInt(partSpecialField.getText());
                }
                catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Only numeric data allowed in numeric fields.");
                    alert.showAndWait();
                    return;
                }

                part = new InHouse(this.part.getId(),
                        name,
                        price,
                        stock,
                        min,
                        max,
                        Integer.parseInt(partSpecialField.getText())
                );
            }

            Inventory.deletePart(this.part);
            Inventory.addPart(part);
        }
        navigateToMainForm();
    }

    /**
     * <p>Navigate back to Main form and discard changes.</p>
     * @param action Provided by event system.
     * @throws IOException
     */
    @FXML
    private void onCancelButton(ActionEvent action) throws IOException {
        navigateToMainForm();
    }

    /**
     * <p>Helper function for navigating back to the Main form.</p>
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
     * <p>Changes the form labels to In-House parts.</p>
     * RUNTIME ERROR: Added validation so that the company name
     * is discarded in the even that the user attempts to modify
     * an outsourced part to be In-House. This was a potential
     * source of data error.
     * @param actionEvent Provided by event system.
     */
    @FXML
    public void onInHouseRadio(ActionEvent actionEvent) {
        partSpecialLabel.setText("Machine ID");
    }

    /**
     * <p>Changes form labels to Outsourced parts.</p>
     * <p>Note: Since a number could theoretically be a company name,
     * there is no need for validation in the event that a
     * user is converting an In-House part to Outsourced.</p>
     * @param actionEvent Provided by event system.
     */
    @FXML
    public void onOutsourcedRadio(ActionEvent actionEvent) {
        partSpecialLabel.setText("Company Name");
    }

}
