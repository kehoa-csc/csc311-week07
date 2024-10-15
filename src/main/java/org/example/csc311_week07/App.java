package org.example.csc311_week07;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Scanner;

import org.example.csc311_week07.db.ConnDbOps;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static ConnDbOps cdbop;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("db_interface_gui"), 807, 535);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));

        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        cdbop = new ConnDbOps();
        Scanner scan = new Scanner(System.in);
        boolean exists;
        int editID;
        int deleteID;
        char input;
        do {
            System.out.println(" ");
            System.out.println("=============== Menu ===============");
            System.out.println("| To start GUI,           press 'g' |");
            System.out.println("| To connect to DB,       press 'c' |");
            System.out.println("| To display all users,   press 'a' |");
            System.out.println("| To insert to the DB,    press 'i' |");
            System.out.println("| To query by name,       press 'q' |");
            System.out.println("| To edit by a user,      press 't' |"); //E, D, and I were taken...
            System.out.println("| To delete a user,       press 'd' |");
            System.out.println("| To exit,                press 'e' |");
            System.out.println("===================================");
            System.out.print("Enter your choice: ");
            input = scan.next().charAt(0);

            switch (input) {
                case 'g':
                     launch(args); //GUI
                    break;

                case 'c':
                    cdbop.connectToDatabase(); //Your existing method
                    break;
                case 'a':
                    cdbop.listAllUsers(); //all users in DB
                    break;

                case 'i':
                    System.out.print("Enter Name: ");
                    String name = scan.next();
                    System.out.print("Enter Email: ");
                    String email = scan.next();
                    System.out.print("Enter Phone: ");
                    String phone = scan.next();
                    System.out.print("Enter Address: ");
                    String address = scan.next();
                    System.out.print("Enter Password: ");
                    String password = scan.next();
                    cdbop.insertUser(name, email, phone, address, password); //Your insertUser method
                    break;
                case 'q':
                    System.out.print("Enter the name to query: ");
                    String queryName = scan.next();
                    cdbop.queryUserByName(queryName); //Your queryUserByName method
                    break;

                case 't':
                    System.out.print("Enter ID of user to edit:");
                    editID = 0;

                    exists = false;
                    while (!exists) {
                        editID = scan.nextInt();
                        exists = cdbop.doesUserExist(editID);
                        if (!exists) {System.out.println("User ID does not exist. Enter valid ID.");}
                    }

                    System.out.print("Enter Name: ");
                    name = scan.next();
                    System.out.print("Enter Email: ");
                    email = scan.next();
                    System.out.print("Enter Phone: ");
                    phone = scan.next();
                    System.out.print("Enter Address: ");
                    address = scan.next();
                    System.out.print("Enter Password: ");
                    password = scan.next();
                    cdbop.editUser(editID,name,email,phone,address,password);
                break;

                case 'd':
                    System.out.print("Enter ID of user to delete:");
                    deleteID = 0;
                    exists = false;
                    while (!exists) {
                        deleteID = scan.nextInt();
                        exists = cdbop.doesUserExist(deleteID);
                        if (!exists) {System.out.println("User ID does not exist. Enter valid ID.");}
                    }
                    cdbop.deleteUser(deleteID);
                break;

                case 'e':
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
            System.out.println(" ");
        } while (input != 'e');

        scan.close();
        
       
    }




}
