package sample;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;

public class Controller {
    @FXML ImageView chosenImageView, greyImageView;
    @FXML Slider hueSlider, saturationSlider, brightnessSlider;
    @FXML Label pleaseClick;
    @FXML AnchorPane chosenColor;
    Image img; PixelReader pr; PixelWriter pw; WritableImage wi;
    Color selectedColor, pixelColor;
    double selectedHue, selectedSaturation, selectedBrightness, hueDifference, saturationDifference, brightnessDifference;
    int width, height;
    int[] pixelArray;
    HashMap<Integer, Integer> fruitClusters = new HashMap<Integer, Integer>();

    // BLACK AND WHITE CONVERSION //
    public Image greyscaleConversion() {
        pixelArray = new int[width*height];

        if (decideRGB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue()) == 1) {
            biggerRedFruitRecog(pr, pw);
        }
        else if (decideRGB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue()) == 2) {
            biggerBlueFruitRecog(pr, pw);
        }

        System.out.println(Arrays.toString(pixelArray));
        img=wi;
        return img;
    }

    // DECIDE WHAT COLOUR THE FRUIT CLICKED ON IS //
    public void biggerRedFruitRecog(PixelReader pr, PixelWriter pw) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixelColor = pr.getColor(x, y);
                if (compareHSB() && pixelColor.getRed() > pixelColor.getBlue() && pixelColor.getRed() > pixelColor.getGreen()) {
                    pw.setColor(x, y, Color.valueOf("#ffffff"));
                    addFruitToArray(y, x);
                } else {
                    pw.setColor(x, y, Color.valueOf("#000000"));
                    addNonFruitToArray(y, x);
                }
            }
        }
    }

    public void newDS() {
        DisjointSet<Integer> ds = new DisjointSet<>();


    }

    public void biggerBlueFruitRecog(PixelReader pr, PixelWriter pw) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixelColor = pr.getColor(x, y);
                if (compareHSB() && pixelColor.getRed() < pixelColor.getBlue() && pixelColor.getGreen() < pixelColor.getBlue()) {
                    pw.setColor(x, y, Color.valueOf("#ffffff"));
                    addFruitToArray(y, x);
                } else {
                    pw.setColor(x, y, Color.valueOf("#000000"));
                    addNonFruitToArray(y, x);
                }
            }
        }
    }

    public void addFruitToArray(int row, int column) {
        int a = (row*width) + column;
        pixelArray[a] = a;
    }

    public void addNonFruitToArray(int row, int column) {
        pixelArray[(row*width)+column] = -1;
    }

    // DECIDES WHICH RGB VALUES TO USE AS FRUITS DEPENDING ON COLOUR CLICKED ON BY USER //
    public int decideRGB(double r, double g, double b) {
        if (r > g && r > b) return 1;
        return 2;
    }

    // RGB STUFF //
    public void getColourAtMouseR(javafx.scene.input.MouseEvent mouseEvent) {
        selectedColor = pr.getColor((int) mouseEvent.getX(), (int) mouseEvent.getY());
        selectedHue=selectedColor.getHue();
        selectedSaturation=selectedColor.getSaturation();
        selectedBrightness=selectedColor.getBrightness();
        System.out.println(selectedColor.getRed() * 255 + " " + selectedColor.getGreen() * 255 + " " + selectedColor.getBlue() * 255);
        if (greyImageView.getImage() != null) {
            displayGreyImage();
        }
    }

    public void displayGreyImage() {
        greyImageView.setImage(greyscaleConversion());
    }

    // SLIDERS //
    public void hueSliderChange() {
        hueDifference = hueSlider.getValue();
        displayGreyImage();
    }

    public void saturationSliderChange() {
        saturationDifference = saturationSlider.getValue();
        displayGreyImage();
    }

    public void brightnessSliderChange() {
        brightnessDifference = brightnessSlider.getValue();
        displayGreyImage();
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

    // IMAGE SETUP //
    public void fileChooser() throws FileNotFoundException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"), new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"));
        File file = fc.showOpenDialog(null);
        imageInitialisation(file);
        setDifferences(360, 0.4, 0.3);
        chosenImageView.setImage(img);
        pleaseClick.setVisible(true);
    }

    public void imageInitialisation(File file) throws FileNotFoundException {
        img = new Image(new FileInputStream(file), (int)chosenImageView.getFitWidth(), (int)chosenImageView.getFitHeight(), false, true);
        width = (int)img.getWidth();
        height = (int)img.getHeight();
        pr=img.getPixelReader();
        wi=new WritableImage(pr, width, height);
        pw=wi.getPixelWriter();
        pixelArray = new int[width*height];
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
