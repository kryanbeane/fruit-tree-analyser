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
    double selectedHue, selectedSaturation, selectedBrightness, hueDifference, saturationDifference, brightnessDifference;

    // IMAGE SETUP //
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

    // RGB STUFF //
    public void getColourAtMouseR(javafx.scene.input.MouseEvent mouseEvent) {
        pixelReader=image.getPixelReader();
        selectedColor = pixelReader.getColor((int) mouseEvent.getX(), (int) mouseEvent.getY());
        System.out.println(selectedColor.getRed()*255 + " " + selectedColor.getGreen()*255 + " " + selectedColor.getBlue()*255);
        if(greyImageView.getImage()!=null) {
            displayGreyImage();
        }
    }
    public int decideRGB() {
        double r=selectedColor.getRed();
        double g=selectedColor.getGreen();
        double b=selectedColor.getBlue();
        if(r>g&&r>b) return 1;
        else if(b>g&&b>r) return 2;
        return 0;
    }



    // BLACK AND WHITE CONVERSION //
    public Image greyscaleConversion() {
        selectedHue = selectedColor.getHue();
        selectedSaturation = selectedColor.getSaturation();
        selectedBrightness = selectedColor.getBrightness();
        updateGreyImageTools();
        if(decideRGB()==1) {
            for(int x=0; x<width; x++) {
                for(int y=0; y<height; y++) {
                    pixelColor=greyPixelReader.getColor(x, y);
                    if(compareHSB() && pixelColor.getRed()>pixelColor.getBlue() && pixelColor.getRed()>pixelColor.getGreen())
                        greyPixelWriter.setColor(x, y, Color.valueOf("#ffffff"));
                    else {
                        greyPixelWriter.setColor(x, y, Color.valueOf("#000000"));
                    }
                }
            }
        } else if(decideRGB()==2) {
            for(int x=0; x<width; x++) {
                for(int y=0; y<height; y++) {
                    pixelColor=greyPixelReader.getColor(x, y);
                    if(compareHSB() && pixelColor.getRed()<pixelColor.getBlue() && pixelColor.getGreen()<pixelColor.getBlue())
                        greyPixelWriter.setColor(x, y, Color.valueOf("#ffffff"));
                    else {
                        greyPixelWriter.setColor(x, y, Color.valueOf("#000000"));
                    }
                }
            }
        }
        greyImage=greyWritableImage;
        return greyImage;
    }
    public void updateGreyImageTools() {
        greyImage=image;                                                      // Updating greyImage
        greyPixelReader=greyImage.getPixelReader();                           // Updating greyPixelReader
        greyWritableImage=new WritableImage(greyPixelReader, width, height);  // Updating greyWritableImage
        greyPixelWriter=greyWritableImage.getPixelWriter();                   // Updating greyPixelWriter
    }
    public void displayGreyImage() {
        greyImageView.setImage(greyscaleConversion());
    }

    // COMPARE HSB //
    public boolean compareHue(double firstHue, double secondHue, double difference) {
        return (Math.abs(firstHue - secondHue) < difference);
    }
    public boolean compareSaturation(double firstSaturation, double secondSaturation, double difference) {
        return (Math.abs(firstSaturation - secondSaturation) < difference);
    }
    public boolean compareBrightness(double firstBrightness, double secondBrightness, double difference) {
        return (Math.abs(firstBrightness - secondBrightness) < difference);
    }
    public boolean compareHSB() {
        return compareSaturation(pixelColor.getSaturation(), selectedSaturation, saturationDifference) && compareBrightness(pixelColor.getBrightness(), selectedBrightness, brightnessDifference) && compareHue(pixelColor.getHue(), selectedHue, hueDifference);
    }

    // SLIDERS //
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

    // EXIT AND RESET //
    public void reset() {
        selectedColor=null;
        chosenImageView.setImage(null);
        greyImageView.setImage(null);
        hueSlider.setValue(0);
        saturationSlider.setValue(0.5);
        brightnessSlider.setValue(0.5);
    }
    public void exit() {
        System.exit(0);
    }

}
