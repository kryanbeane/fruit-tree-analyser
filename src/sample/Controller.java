package sample;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Controller {
    @FXML ImageView chosenImageView, greyImageView;
    @FXML Slider hueSlider, saturationSlider, brightnessSlider;
    @FXML Label pleaseClick;
    static Color selectedColor;
    Image img; PixelReader pr; PixelWriter pw; WritableImage wi;

    // IMAGE SETUP //
    public void fileChooser() {
        try {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"), new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"));
            File file = fc.showOpenDialog(null);
            img = new Image(new FileInputStream(file), chosenImageView.getFitWidth(), chosenImageView.getFitHeight(), false, true, pr, pw, wi);
            img.setDifferences(360, 0.4, 0.3);
            displayImage(img);

        } catch (FileNotFoundException exception) {
            System.out.println("File not found, try again!");
        }
    }

    public void displayImage(Image img) {
        chosenImageView.setImage(img);
        pleaseClick.setVisible(true);
    }

    // RGB STUFF //
    public void getColourAtMouseR(javafx.scene.input.MouseEvent mouseEvent) {
        selectedColor = img.pixelReader.getColor((int) mouseEvent.getX(), (int) mouseEvent.getY());
        img.selectedHue=selectedColor.getHue();
        img.selectedSaturation=selectedColor.getSaturation();
        img.selectedBrightness=selectedColor.getBrightness();
        System.out.println(selectedColor.getRed() * 255 + " " + selectedColor.getGreen() * 255 + " " + selectedColor.getBlue() * 255);
        if (greyImageView.getImage() != null) {
            displayGreyImage();
        }
    }

    public void displayGreyImage() {
        greyImageView.setImage(img.greyscaleConversion());
    }

    // SLIDERS //
    public void hueSliderChange() {
        img.hueDifference = hueSlider.getValue();
        displayGreyImage();
    }

    public void saturationSliderChange() {
        img.saturationDifference = saturationSlider.getValue();
        displayGreyImage();
    }

    public void brightnessSliderChange() {
        img.brightnessDifference = brightnessSlider.getValue();
        displayGreyImage();
    }

    // EXIT AND RESET //
    public void reset() {
        selectedColor = null;
        chosenImageView.setImage(null);
        greyImageView.setImage(null);
        hueSlider.setValue(0);
        saturationSlider.setValue(0.5);
        brightnessSlider.setValue(0.5);
        pleaseClick.setVisible(false);
    }

    public void exit() {
        System.exit(0);
    }

}
