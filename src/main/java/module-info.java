module org.example.csc311_week07 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.csc311_week07 to javafx.fxml;
    exports org.example.csc311_week07;
}