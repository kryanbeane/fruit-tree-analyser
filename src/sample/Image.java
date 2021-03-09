package sample;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import java.io.InputStream;

public class Image extends javafx.scene.image.Image {
    public PixelReader pixelReader;
    public PixelWriter pixelWriter;
    public WritableImage writableImage;
    Color selectedColor;
    Color pixelColor;
    double selectedHue, selectedSaturation, selectedBrightness, hueDifference, saturationDifference, brightnessDifference;

    public Image(InputStream inputStream, double v, double v1, boolean b, boolean b1, PixelReader pixelReader, PixelWriter pixelWriter, WritableImage writableImage) {
        super(inputStream, v, v1, b, b1);
        this.pixelReader=this.getPixelReader();
        this.writableImage=new WritableImage(this.pixelReader, (int)v, (int)v1);
        this.pixelWriter=this.writableImage.getPixelWriter();
    }

    // BLACK AND WHITE CONVERSION //
    public WritableImage greyscaleConversion() {
        selectedColor=Controller.selectedColor;
        if (decideRGB() == 1) {
            biggerRedFruitRecog(this.pixelReader, this.pixelWriter);
        } else if (decideRGB() == 2) {
            biggerBlueFruitRecog(this.pixelReader, this.pixelWriter);
        }
        return this.writableImage;
    }

    // DECIDE WHAT COLOUR THE FRUIT CLICKED ON IS //
    public void biggerRedFruitRecog(PixelReader pr, PixelWriter pw) {
        for (int x = 0; x < this.getWidth(); x++) {
            for (int y = 0; y < this.getHeight(); y++) {
                pixelColor = pr.getColor(x, y);
                if (compareHSB() && pixelColor.getRed() > pixelColor.getBlue() && pixelColor.getRed() > pixelColor.getGreen())
                    pw.setColor(x, y, Color.valueOf("#ffffff"));
                else {
                    pw.setColor(x, y, Color.valueOf("#000000"));
                }
            }
        }
    }

    public void biggerBlueFruitRecog(PixelReader pr, PixelWriter pw) {
        for (int x = 0; x < this.getWidth(); x++) {
            for (int y = 0; y < this.getHeight(); y++) {
                pixelColor = pr.getColor(x, y);
                if (compareHSB() && pixelColor.getRed() < pixelColor.getBlue() && pixelColor.getGreen() < pixelColor.getBlue())
                    pw.setColor(x, y, Color.valueOf("#ffffff"));
                else {
                    pw.setColor(x, y, Color.valueOf("#000000"));
                }
            }
        }
    }

    // SET DIFFERENCES, USED FOR B&W CONVERSIONS //
    public void setHueDifference(double newHue) {
        hueDifference=newHue;
    }

    public void setSaturationDifference(double newSaturation) {
        saturationDifference=newSaturation;
    }

    public void setBrightnessDifference(double newBrightness) {
        brightnessDifference=newBrightness;
    }

    public void setDifferences(double newHue, double newSaturation, double newBrightness) {
        setHueDifference(newHue);
        setSaturationDifference(newSaturation);
        setBrightnessDifference(newBrightness);
    }

    // DECIDES WHICH RGB VALUES TO USE AS FRUITS DEPENDING ON COLOUR CLICKED ON BY USER //
    public int decideRGB() {
        double r = selectedColor.getRed(), g = selectedColor.getGreen(), b = selectedColor.getBlue();
        if (r > g && r > b) return 1;
        return 2;
    }

    // COMPARE HSB METHODS //
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

    /*    public void updateGreyImageTools() {
        this.wi = this;
        greyPixelReader = greyImage.getPixelReader();
        greyWritableImage = new WritableImage(greyPixelReader, width, height);
        greyPixelWriter = greyWritableImage.getPixelWriter();
    }*/

}
