package sample;
import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.File;

public class Controller {
    Double red, green, blue;
    Image image;
    int width, height;
    PixelReader pr;
    PixelWriter originalPW, greyPW;
    WritableImage originalWI, greyWI;
    @FXML BorderPane homeTab, greyscaleTab, fruitRecogTab, settingsTab;
    @FXML ImageView originalImageView;

    public void openImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Image");
        // Exempts file non-JPG and non-PNG files from file chooser dialogue box
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        fc.getExtensionFilters().addAll (extFilterPNG, extFilterJPG);
        // Opens dialogue
        File file = fc.showOpenDialog(null);

        try {
            if (file != null) {
                Image image = new Image(file.toURI().toString());
                displayImage(image);
            }
        } catch(NullPointerException ignored) {
            System.out.println("Didn't work");
        }
    }

    public void displayImage(Image chosenFile) {
        image = chosenFile;
        width = (int) image.getWidth();
        height = (int) image.getHeight();
        pr=image.getPixelReader();
        originalWI = new WritableImage(pr, width, height);
        originalPW = originalWI.getPixelWriter();
        originalImageView.setImage(image);
    }

    public Image greyscaleConversion(Image originalImage) {
        return null;
    }

    public void homeButton() {
        homeTab.toFront();
        greyscaleTab.toBack();
        fruitRecogTab.toBack();
        settingsTab.toBack();
    }

    public void greyscaleButton() {
        greyscaleTab.toFront();
        homeTab.toBack();
        fruitRecogTab.toBack();
        settingsTab.toBack();
    }

    public void fruitRecogButton() {
        fruitRecogTab.toFront();
        homeTab.toBack();
        greyscaleTab.toBack();
        settingsTab.toBack();
    }

    public void settingsButton() {
        settingsTab.toFront();
        homeTab.toBack();
        greyscaleTab.toBack();
        fruitRecogTab.toBack();
    }

    public void exit(){
        System.exit(0);
    }

    public void getColourAtMouseR(javafx.scene.input.MouseEvent mouseEvent) {
        Color color = pr.getColor((int) mouseEvent.getX(), (int) mouseEvent.getY());
        red=color.getRed()*255;
        green=color.getGreen()*255;
        blue=color.getBlue()*255;
        System.out.println(red + " " + green + " " + blue);
    }
}
