package org.example.csc311_week07;

import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

    //Moves to main interface when login or register button is pressed.
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("db_interface_gui");
    }
}