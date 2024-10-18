package org.example.csc311_week07;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Array;

import static org.example.csc311_week07.App.cdbop;
import static org.example.csc311_week07.App.scene;

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
    MenuItem themeMenuItem;
    boolean darkMode = false;

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

    //Connects to the database.
    @FXML
    private void connectToDB() {
        editingPhase = 0;
        message.setText("Attempting to connect to database...");
        boolean result = cdbop.connectToDatabase();
        if (result) { //Gives a proper message depending on the result.
            message.setText("Connected to database successfully.");
        } else {
            message.setText("Failed to connect to database.");
        }
    }

    //Gets all users from the database and displays them in the table.
    @FXML
    private void displayAllUsers() {
        editingPhase = 0;
        //Gets the user list from the database.
        ObservableList<User> users = cdbop.listAllUsers(true);

        //Formats the tables to accept data.
        tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tv_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        tv_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tv_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        tv_password.setCellValueFactory(new PropertyValueFactory<>("password"));

        //Updates the table with the newly fetched data.
        tv.setItems(users);
    }

    //Inserts a user into the database with the given information in the text boxes.
    @FXML
    private void insertUser() {
        editingPhase = 0;
        message.setText("Attempting to insert user...");
        //Checks that none of the fields are empty.
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || phoneField.getText().isEmpty() || addressField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            message.setText("Please provide info for all fields.");
            return;
        }
        try { //Inserts the user with information from the text fields, and refreshes the table.
            cdbop.insertUser(
                    nameField.getText(), emailField.getText(), phoneField.getText(), addressField.getText(), passwordField.getText()
            );
            message.setText("User inserted successfully.");
            displayAllUsers();
        } catch (Exception e) {
            message.setText("Failed to insert user. Do you have duplicate values?");
        }
    }

    //Searches for a user by the name given in the name text box.
    @FXML
    private void queryUser() {
        editingPhase = 0;
        //Checks if name field is empty.
        if (nameField.getText().isEmpty()) {
            message.setText("Please enter name to query by.");
        } else { //Fetches the list of users filtered by name from the database.
            message.setText("Searching...");
            ObservableList<User> users = cdbop.queryUserByName(nameField.getText(),true);
            //Formats the tables to accept data.
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_name.setCellValueFactory(new PropertyValueFactory<>("name"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
            tv_address.setCellValueFactory(new PropertyValueFactory<>("address"));
            tv_password.setCellValueFactory(new PropertyValueFactory<>("password"));
            //Updates the table with the newly fetched data.
            tv.setItems(users);
            //Displays a message to confirm the search is complete.
            message.setText("Here are users by the name of \"" + nameField.getText() + "\"...");
        }
    }

    //Lets the user re-enter details of a user by their ID.
    @FXML private void editUser() {
        switch(editingPhase) {
            case 0: //First button press
                //Refreshes database table and disables other buttons + text fields.
                displayAllUsers();
                toggleOthers(true);
                deleteButton.setDisable(true);
                deleteMenu.setDisable(true);

                //Prompts user for the ID, and sets the flag to know that the program is currently editing something.
                message.setText("Type in ID of user to edit, then press edit again. Type c to cancel.");
                nameField.setText("");
                nameField.requestFocus();
                nameField.setPromptText("ID");
                editingPhase = 1;
            break;
            case 1: //Second button press
                if (nameField.getText().isEmpty()) {
                    message.setText("ID field empty. Please type in ID and press edit again.");
                } else {
                    try {
                        //Goes back to normal and cancels operation if c is typed in the box
                        if (nameField.getText().equals("c")) {
                            deleting = false;
                            nameField.setPromptText("Name");
                            toggleOthers(false);
                            deleteButton.setDisable(false);
                            deleteMenu.setDisable(false);
                            message.setText("Cancelled editing of user.");
                            editingPhase = 0;
                        } else {
                            //Checks that user with the given ID exists
                            editingID = Integer.parseInt(nameField.getText());
                            if (!cdbop.doesUserExist(editingID)) {
                                message.setText("User with ID " + editingID + " does not exist.");
                                return;
                            }
                            //Moves onto next step, disables other fields and clears information in them
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
                        //If ID is not an integer, ask again.
                        message.setText("Provided ID is not a number. Please type in an ID and press edit again, or c to cancel.");
                    }
                }
            break;
            case 2: //Third and final button press
                //Checks if any of the fields are empty
                if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || phoneField.getText().isEmpty() || addressField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                    message.setText("Editing " + editingID + ": Please provide info for all fields.");
                    return;
                } else {
                    //Edits user based on the provided info in fields, refreshes the table, and re-enables other fields/buttons
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

    //Prompts user for an ID of user to delete, then deletes it.
    @FXML
    private void deleteUser() {
        editingPhase = 0;
        if (!deleting) { //First button press
            //Refreshes database table and disables other buttons + text fields.
            displayAllUsers();
            toggleOthers(true);
            editButton.setDisable(true);
            editMenu.setDisable(true);

            //Prompts user for the ID, and sets the flag to know that the program is currently deleting something.
            message.setText("Type in ID of user to delete, then press delete again. Type c to cancel.");
            nameField.setText("");
            nameField.requestFocus();
            nameField.setPromptText("ID");
            deleting = true;
        } else { //Second button press
            if (nameField.getText().isEmpty()) {
                message.setText("ID field empty. Please type in ID and press delete again.");
            } else {
                try {
                    //Goes back to normal and cancels operation if c is typed in the box
                    if (nameField.getText().equals("c")) {
                        deleting = false;
                        nameField.setPromptText("Name");
                        message.setText("Cancelled deletion of user.");

                        toggleOthers(false);
                        editButton.setDisable(false);
                        editMenu.setDisable(false);
                    } else {
                        //Gets ID and checks if user exists, then if so, deletes it and enables everything again.
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
                    //If ID is not an integer, ask again.
                    message.setText("Provided ID is not a number. Please type in an ID and press delete again, or c to cancel.");
                }
            }
        }
        displayAllUsers();
    }

    //A method the edit and delete methods use to disable the other buttons and text fields, for clarity.
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

    //Closes the application from Menu close option.
    @FXML
    private void closeApp() {
        System.exit(0);
    }

    //Turns on labels for keyboard shorcuts on the buttons.
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

    //Toggles the interface between a dark and light theme.
    @FXML
    private void toggleTheme() {
        if (!darkMode) {
            scene.getStylesheets().add(getClass().getResource("darkmode.css").toExternalForm());
            darkMode = true;
            themeMenuItem.setText("Light Mode");
        } else {
            scene.getStylesheets().clear();
            darkMode = false;
            themeMenuItem.setText("Dark Mode");
        }
    }

    //Displays an about message in the log.
    @FXML
    private void aboutMessage() {
        message.setText("Week 07 Assignment. Written by Andrew Kehoe, forked from code by Moaath Alrajab. (c)2024, All rights reserved.");
    }
}
