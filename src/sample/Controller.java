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
    double selectedSaturation, selectedBrightness;

    public Image fileChooser() {
        try {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"), new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"));
            File file = fc.showOpenDialog(null);
            return image = new Image(new FileInputStream(file), chosenImageView.getFitWidth(), chosenImageView.getFitHeight(), false, true);
        }
        catch (FileNotFoundException exception){
            System.out.println("File not found, try again!");
            return null;
        }
    }

    public void displayImage() {
        chosenImageView.setImage(fileChooser());
        width=(int)image.getWidth();
        height=(int)image.getHeight();
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
        selectedSaturation = selectedColor.getSaturation();
        selectedBrightness = selectedColor.getBrightness();
        updateGreyImageTools();

        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                pixelColor=greyPixelReader.getColor(x, y);
                if(compareSaturation(pixelColor.getSaturation(), selectedSaturation) && compareBrightness(pixelColor.getBrightness(), selectedBrightness)
                                && pixelColor.getRed() > pixelColor.getBlue() && pixelColor.getRed() > pixelColor.getGreen())
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

    public boolean compareSaturation(double firstSaturation, double secondSaturation) {
        return (Math.abs(firstSaturation - secondSaturation) < .4);
    }

    public boolean compareBrightness(double firstBrightness, double secondBrightness) {
        return (Math.abs(firstBrightness - secondBrightness) < .3);
    }

//
//        for (int x = 1; x < width; x++) {
//            for (int y = 1; y < height; y++) {
//                currentColor = greyPR.getColor(x, y);
//                setCurrentHSB();
//
//                if (compareHue(currentHue, selectedHue) && compareSaturation(currentSaturation, selectedSaturation) && compareBrightness(currentBrightness, selectedBrightness)) {
//                    greyPW.setColor(x, y, Color.valueOf("#000000"));
//                } else {
//                    greyPW.setColor(x, y, Color.valueOf("#ffffff"));
//                }
//            }
//        }

//        greyImage = greyWI;
//        return greyImage;
//    }
//

//
//
//    public void displayImage() {
//        width = (int) image.getWidth();
//        height = (int) image.getHeight();
//        PixelReader originalPR = image.getPixelReader();
//        originalWI = new WritableImage(originalPR, width, height);
//        originalPW = originalWI.getPixelWriter();
//        originalImageView.setImage(image);
//    }
//
//    public void setCurrentHSB() {
//        currentHue = currentColor.getHue();
//        currentSaturation = currentColor.getSaturation();
//        currentBrightness = currentColor.getBrightness();
//    }
//
//    public void setGreyscale() {
//        Image newGreyImage = greyScaleConversion();
//        greyScaleImageView.setImage(newGreyImage);
//    }

    public void exit() {
        System.exit(0);
    }

}
