package sample;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML Button paneBtn, paneBtn2;
    @FXML AnchorPane tab1, tab2;

//    public void openImage() {
//        FileChooser fc = new FileChooser();
//        fc.setTitle("Open Image");
//        // Exempts file non-JPG and non-PNG files from file chooser dialogue box
//        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
//        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
//        fc.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
//        File file = fc.showOpenDialog(null);
//
//        if (file != null) {
//            Image image = new Image(file.toURI().toString());
//            fxMainImageView.setImage(image);
//            //displayImage(image);
//        }
//    }

    public void button1() {

    }

    public void button2() {

    }

    public void exit(){
        System.exit(0);
    }

}
