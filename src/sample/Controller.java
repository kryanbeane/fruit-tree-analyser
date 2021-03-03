package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Controller {
    int width, height;
    Color selectedColor, pixelColor;
    @FXML ImageView chosenImageView, greyImageView;
    @FXML Slider hueSlider, saturationSlider, brightnessSlider;
    Image image, greyImage; PixelReader pixelReader, greyPixelReader; PixelWriter pixelWriter, greyPixelWriter; WritableImage writableImage, greyWritableImage;
    double selectedHue, selectedSaturation, selectedBrightness;
    double hueDifference, saturationDifference, brightnessDifference;

    public void fileChooser() {
        try {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"), new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"));
            File file = fc.showOpenDialog(null);
            image = new Image(new FileInputStream(file), chosenImageView.getFitWidth(), chosenImageView.getFitHeight(), false, true);
            saturationDifference=0.4; brightnessDifference=0.3; hueDifference=360;
            displayImage(image);
        }
        catch (FileNotFoundException exception){
            System.out.println("File not found, try again!");
        }
    }

    public void displayImage(Image img) {
        chosenImageView.setImage(img);
        width=(int)img.getWidth();
        height=(int)img.getHeight();
    }

    public void getColourAtMouseR(javafx.scene.input.MouseEvent mouseEvent) {
        pixelReader=image.getPixelReader();
        selectedColor = pixelReader.getColor((int) mouseEvent.getX(), (int) mouseEvent.getY());
        System.out.println(selectedColor.getRed()*255 + " " + selectedColor.getGreen()*255 + " " + selectedColor.getBlue()*255);
        if(greyImageView.getImage()!=null) {
            displayGreyImage();
        }
    }

    public void updateGreyImageTools() {
        greyImage=image;                                                      // Updating greyImage
        greyPixelReader=greyImage.getPixelReader();                           // Updating greyPixelReader
        greyWritableImage=new WritableImage(greyPixelReader, width, height);  // Updating greyWritableImage
        greyPixelWriter=greyWritableImage.getPixelWriter();                   // Updating greyPixelWriter
    }

    public Image greyscaleConversion() {
        selectedHue = selectedColor.getHue();
        selectedSaturation = selectedColor.getSaturation();
        selectedBrightness = selectedColor.getBrightness();
        updateGreyImageTools();

        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                pixelColor=greyPixelReader.getColor(x, y);
                if(compareSaturation(pixelColor.getSaturation(), selectedSaturation) && compareBrightness(pixelColor.getBrightness(), selectedBrightness)
                                && compareHue(pixelColor.getHue(), selectedHue) && pixelColor.getRed() > pixelColor.getBlue() && pixelColor.getRed() > pixelColor.getGreen())
                    greyPixelWriter.setColor(x, y, Color.valueOf("#ffffff"));
                else {
                    greyPixelWriter.setColor(x, y, Color.valueOf("#000000"));
                }
            }
        }
        greyImage=greyWritableImage;
        return greyImage;
    }

    public void displayGreyImage() {
        greyImageView.setImage(greyscaleConversion());
    }

    public boolean compareHue(double firstHue, double secondHue) {
        return (Math.abs(firstHue - secondHue) < hueDifference);
    }

    public boolean compareSaturation(double firstSaturation, double secondSaturation) {
        return (Math.abs(firstSaturation - secondSaturation) < saturationDifference);
    }

    public boolean compareBrightness(double firstBrightness, double secondBrightness) {
        return (Math.abs(firstBrightness - secondBrightness) < brightnessDifference);
    }


    public void hueSliderChange() {
        hueDifference=hueSlider.getValue();
        displayGreyImage();
    }

    public void saturationSliderChange() {
        saturationDifference=saturationSlider.getValue();
        displayGreyImage();
    }

    public void brightnessSliderChange() {
        brightnessDifference=brightnessSlider.getValue();
        displayGreyImage();
    }

    public void exit() {
        System.exit(0);
    }

}
