package view;

/**
 * Sample Skeleton for "simple.fxml" Controller Class
 * Use copy/paste to copy paste this code into your favorite IDE
 **/

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;


public class MainWindowController implements Initializable {

    @FXML //  fx:id="myButton"
    private Button myButton; // Value injected by FXMLLoader


    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        //assert myButton != null : "fx:id=\"myButton\" was not injected: check your FXML file 'simple.fxml'.";

        // initialize your logic here: all @FXML variables will have been injected

    }

}