package sample;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;

public class Controller {
    Color selectedColor, currentColor;
    double selectedHue, currentHue, selectedSaturation, currentSaturation, selectedBrightness, currentBrightness;
    Image image, greyImage;
    int width, height;
    PixelReader originalPR, greyPR;
    PixelWriter originalPW, greyPW;
    WritableImage originalWI, greyWI;
    @FXML BorderPane homeTab, greyscaleTab, fruitRecogTab, settingsTab;
    @FXML ImageView originalImageView, greyScaleImageView;
    @FXML Slider hueSlider, saturationSlider, brightnessSlider;

    public boolean compareHue(double firstHue, double secondHue) {
        return (Math.abs(firstHue-secondHue) < 40);
    }

    public boolean compareSaturation(double firstSaturation, double secondSaturation) {
        return (Math.abs(firstSaturation-secondSaturation) < .3);
    }

    public boolean compareBrightness(double firstBrightness, double secondBrightness) {
        return (Math.abs(firstBrightness-secondBrightness) < .3);
    }

    public Image greyScaleConversion() {
        greyImage=image;
        greyPR=greyImage.getPixelReader();
        greyWI = new WritableImage(greyPR, width, height);
        greyPW = greyWI.getPixelWriter();
        greyPR=greyWI.getPixelReader();
        selectedHue=selectedColor.getHue();
        selectedSaturation=selectedColor.getSaturation();
        selectedBrightness=selectedColor.getBrightness();

        for(int x=1; x<width; x++) {
            for(int y=1; y<height; y++) {
                currentColor=greyPR.getColor(x, y);
                setCurrentHSB();

                if(compareHue(currentHue, selectedHue) && compareSaturation(currentSaturation, selectedSaturation) && compareBrightness(currentBrightness, selectedBrightness)) {
                    greyPW.setColor(x, y, Color.valueOf("#000000"));
                } else {
                    greyPW.setColor(x, y, Color.valueOf("#ffffff"));
                }
            }
        }
        greyImage=greyWI;
        return greyImage;
    }

    public Color getColourAtMouseR(javafx.scene.input.MouseEvent mouseEvent) {
        return selectedColor = originalPR.getColor ((int) mouseEvent.getX(), (int) mouseEvent.getY());
    }

    public void openImage() {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Open Image");
            // Exempts file non-JPG and non-PNG files from file chooser dialogue box
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
            fc.getExtensionFilters().addAll (extFilterPNG, extFilterJPG);
            // Opens dialogue
            File file = fc.showOpenDialog(null);

            if (file != null) {
                image = new Image(new FileInputStream(file), originalImageView.getFitWidth(), originalImageView.getFitHeight(), false, true);
                displayImage();
            }
        } catch(Exception exception) {
            System.out.println("Image load didn't work");
        }
    }
    public void displayImage() {
        width = (int) image.getWidth();
        height = (int) image.getHeight();
        PixelReader originalPR=image.getPixelReader();
        originalWI = new WritableImage(originalPR, width, height);
        originalPW = originalWI.getPixelWriter();
        originalImageView.setImage(image);
    }
    public void setCurrentHSB() {
        currentHue=currentColor.getHue();
        currentSaturation=currentColor.getSaturation();
        currentBrightness=currentColor.getBrightness();
    }
    public void setGreyscale() {
        Image newGreyImage = greyScaleConversion();
        greyScaleImageView.setImage(newGreyImage);
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

}
