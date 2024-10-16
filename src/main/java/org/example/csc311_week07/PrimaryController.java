package org.example.csc311_week07;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Array;

import static org.example.csc311_week07.App.cdbop;

public class PrimaryController {

    @FXML
    Button connectButton;
    @FXML
    Button displayButton;
    @FXML
    Button insertButton;
    @FXML
    Button queryButton;
    @FXML
    Button editButton;
    @FXML
    Button deleteButton;
    boolean showShortcuts = false;
    @FXML
    MenuItem connectMenu;
    @FXML
    MenuItem displayMenu;
    @FXML
    MenuItem insertMenu;
    @FXML
    MenuItem queryMenu;
    @FXML
    MenuItem editMenu;
    @FXML
    MenuItem deleteMenu;

    @FXML
    TableView<User> tv;
    @FXML
    TableColumn<User, Integer> tv_id;
    @FXML
    TableColumn<User, String> tv_name;
    @FXML
    TableColumn<User, String> tv_email;
    @FXML
    TableColumn<User, String> tv_phone;
    @FXML
    TableColumn<User, String> tv_address;
    @FXML
    TableColumn<User, String> tv_password;

    @FXML
    TextField nameField;
    boolean deleting = false;
    int editingPhase = 0;
    int editingID = -1;

    @FXML
    TextField emailField;
    @FXML
    TextField phoneField;
    @FXML
    TextField addressField;
    @FXML
    TextField passwordField;

    @FXML
    TextField message;


    @FXML
    private void connectToDB() {
        editingPhase = 0;
        message.setText("Attempting to connect to database...");
        boolean result = cdbop.connectToDatabase();
        if (result) {
            message.setText("Connected to database successfully.");
        } else {
            message.setText("Failed to connect to database.");
        }
    }

    @FXML
    private void displayAllUsers() {
        editingPhase = 0;
        ObservableList<User> users = cdbop.listAllUsers(true);

        tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tv_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        tv_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tv_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        tv_password.setCellValueFactory(new PropertyValueFactory<>("password"));
        tv.setItems(users);
    }

    @FXML
    private void insertUser() {
        editingPhase = 0;
        message.setText("Attempting to insert user...");
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || phoneField.getText().isEmpty() || addressField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            message.setText("Please provide info for all fields.");
            return;
        }
        try {
            cdbop.insertUser(
                    nameField.getText(), emailField.getText(), phoneField.getText(), addressField.getText(), passwordField.getText()
            );
            message.setText("User inserted successfully.");
            displayAllUsers();
        } catch (Exception e) {
            message.setText("Failed to insert user. Do you have duplicate values?");
        }
    }

    @FXML
    private void queryUser() {
        editingPhase = 0;
        if (nameField.getText().isEmpty()) {
            message.setText("Please enter name to query by.");
        } else {
            ObservableList<User> users = cdbop.queryUserByName(nameField.getText(),true);

            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_name.setCellValueFactory(new PropertyValueFactory<>("name"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
            tv_address.setCellValueFactory(new PropertyValueFactory<>("address"));
            tv_password.setCellValueFactory(new PropertyValueFactory<>("password"));
            tv.setItems(users);

            message.setText("Here are users by the name of \"" + nameField.getText() + "\"...");
        }
    }
    
    @FXML private void editUser() {
        switch(editingPhase) {
            case 0:
                displayAllUsers();
                toggleOthers(true);
                deleteButton.setDisable(true);
                deleteMenu.setDisable(true);

                message.setText("Type in ID of user to edit, then press edit again. Type c to cancel.");
                nameField.setText("");
                nameField.requestFocus();
                nameField.setPromptText("ID");
                editingPhase = 1;
            break;
            case 1:
                if (nameField.getText().isEmpty()) {
                    message.setText("ID field empty. Please type in ID and press edit again.");
                } else {
                    try {
                        if (nameField.getText().equals("c")) {
                            deleting = false;
                            nameField.setPromptText("Name");
                            toggleOthers(false);
                            deleteButton.setDisable(false);
                            deleteMenu.setDisable(false);
                            message.setText("Cancelled editing of user.");
                            editingPhase = 0;
                        } else {
                            editingID = Integer.parseInt(nameField.getText());
                            if (!cdbop.doesUserExist(editingID)) {
                                message.setText("User with ID " + editingID + " does not exist.");
                                return;
                            }
                            message.setText("Editing ID " + editingID + ". Provide new information and press edit again.");
                            emailField.setDisable(false);
                            phoneField.setDisable(false);
                            addressField.setDisable(false);
                            passwordField.setDisable(false);

                            nameField.setText("");
                            nameField.requestFocus();
                            nameField.setPromptText("Name");
                            emailField.setText("");
                            phoneField.setText("");
                            addressField.setText("");
                            passwordField.setText("");
                            editingPhase = 2;
                        }
                    } catch (NumberFormatException e) {
                        message.setText("Provided ID is not a number. Please type in an ID and press edit again, or c to cancel.");
                    }
                }
            break;
            case 2:
                if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || phoneField.getText().isEmpty() || addressField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                    message.setText("Editing " + editingID + ": Please provide info for all fields.");
                    return;
                } else {
                    cdbop.editUser(editingID,nameField.getText(),emailField.getText(),phoneField.getText(),addressField.getText(),passwordField.getText());
                    message.setText("User successfully edited.");
                    displayAllUsers();
                    editingPhase = 0;
                    editingID = -1;
                    toggleOthers(false);
                    deleteButton.setDisable(false);
                    deleteMenu.setDisable(false);
                }
            break;
            default:
                editingPhase = 0;
        }
    }

    @FXML
    private void deleteUser() {
        editingPhase = 0;
        if (!deleting) { //first press
            displayAllUsers();
            toggleOthers(true);
            editButton.setDisable(true);
            editMenu.setDisable(true);

            message.setText("Type in ID of user to delete, then press delete again. Type c to cancel.");
            nameField.setText("");
            nameField.requestFocus();
            nameField.setPromptText("ID");
            deleting = true;
        } else { //second press
            if (nameField.getText().isEmpty()) {
                message.setText("ID field empty. Please type in ID and press delete again.");
            } else {
                try {
                    if (nameField.getText().equals("c")) {
                        deleting = false;
                        nameField.setPromptText("Name");
                        message.setText("Cancelled deletion of user.");

                        toggleOthers(false);
                        editButton.setDisable(false);
                        editMenu.setDisable(false);
                    } else {
                        int id = Integer.parseInt(nameField.getText());
                        if (!cdbop.doesUserExist(id)) {
                            message.setText("User with ID " + id + " does not exist.");
                            return;
                        }
                        cdbop.deleteUser(id);
                        message.setText("User " + id + " successfully deleted.");

                        toggleOthers(false);
                        editButton.setDisable(false);
                        editMenu.setDisable(false);
                    }
                }
                catch (NumberFormatException e) {
                    message.setText("Provided ID is not a number. Please type in an ID and press delete again, or c to cancel.");
                }
            }
        }
        displayAllUsers();
    }

    private void toggleOthers(boolean disabled) {
        connectButton.setDisable(disabled);
        displayButton.setDisable(disabled);
        insertButton.setDisable(disabled);
        queryButton.setDisable(disabled);
        emailField.setDisable(disabled);
        phoneField.setDisable(disabled);
        addressField.setDisable(disabled);
        passwordField.setDisable(disabled);
        connectMenu.setDisable(disabled);
        displayMenu.setDisable(disabled);
        insertMenu.setDisable(disabled);
        queryMenu.setDisable(disabled);
    }

    @FXML
    private void closeApp() {
        System.exit(0);
    }

    @FXML
    private void shortcuts(){
        String prefix;
        Button[] buttons = {connectButton, displayButton, insertButton, queryButton, editButton, deleteButton};
        char[] letters = {'C','A','I','Q','T','D'};
        String[] ogLabels = {"Connect to DB","Display all users","Insert User","Query by Name","Edit User","Delete User"};
        if (!showShortcuts) {
            if (System.getProperty("os.name").equals("Mac OS X")) {
                prefix = "Cmd+";
            } else {
                prefix = "Ctrl+";
            }
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setText(buttons[i].getText()+" ("+prefix+letters[i]+")");
            }
            showShortcuts = true;
        } else {
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setText(ogLabels[i]);
            }
            showShortcuts = false;
        }
    }
    /*@FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }*/
}
