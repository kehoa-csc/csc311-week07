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
    boolean gettingID = false;
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
        //Cancel deletion
        nameField.setPromptText("Name");
        gettingID = false;

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
        //Cancel delete user
        nameField.setPromptText("Name");
        gettingID = false;

        message.setText("Looking for users by the name of \"" + nameField.getText() + "\"...");
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
        }
    }

    @FXML
    private void deleteUser() {
        if (!gettingID) { //first press
            message.setText("Type in ID of user to delete, then press delete again. Type c to cancel.");
            nameField.setText("");
            nameField.requestFocus();
            nameField.setPromptText("ID");
            gettingID = true;
        } else { //second press
            if (nameField.getText().isEmpty()) {
                message.setText("ID field empty. Please type in ID and press delete again.");
            } else {
                try {
                    if (nameField.getText().equals("c")) {
                        gettingID = false;
                        nameField.setPromptText("Name");
                        message.setText("Cancelled deletion of user.");
                    } else {
                        int id = Integer.parseInt(nameField.getText());
                        if (!cdbop.doesUserExist(id)) {
                            message.setText("User with ID " + id + " does not exist.");
                            return;
                        }
                        cdbop.deleteUser(id);
                        message.setText("User " + id + " successfully deleted.");
                    }
                }
                catch (NumberFormatException e) {
                    message.setText("ID is not a number. Please type in ID and press delete again.");
                }
            }
        }
        displayAllUsers();
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
