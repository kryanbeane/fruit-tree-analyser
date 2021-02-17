package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML ImageView fxMainImageView;

    /**
     *  JAVA FX INTERACTIVE METHODS
     */

    public void openImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Image");
        // Exempts file non-JPG and non-PNG files from file chooser dialogue box
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fc.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        File file = fc.showOpenDialog(null);

        if (file!= null) {
            Image image = new Image(file.toURI().toString());
            fxMainImageView.setImage(image);
            //displayImage(image);
        }
    }

}
