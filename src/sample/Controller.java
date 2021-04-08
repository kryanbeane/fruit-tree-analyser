package sample;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Controller {
    @FXML ImageView chosenImageView, greyImageView;
    @FXML Slider hueSlider, saturationSlider, brightnessSlider;
    @FXML Label pleaseClick;
    double selectedHue, selectedSaturation, selectedBrightness;
    double hueDifference, saturationDifference, brightnessDifference;
    int width, height;
    int[] pixelArray;
    HashMap<Integer, ArrayList<Integer>> fruitClusters = new HashMap<>();
    Image img, gsImg; PixelReader pr, gsPr; PixelWriter pw, gsPw; WritableImage wi, gsWi;
    Color selectedColor, pixelColor;

    public int calcFurtherLeftPixel(int posA, int posB, int w) {
        return posA%w < posB%w ? posA : posB;
    }

    public int calcFurtherRightPixel(int posA, int posB, int w) {
        return posA%w > posB%w ? posA : posB;
    }

    public void drawClusterBorder(int root, HashMap<Integer, ArrayList<Integer>> fruitClusters, int width) {
        List<Integer> tmpList = fruitClusters.get(root);
        int fLeft =root, fRight =tmpList.get(tmpList.size()-1), bottom =tmpList.get(tmpList.size()-1);
        for(int i : tmpList) {
            fLeft = i < tmpList.size() ? calcFurtherLeftPixel(fLeft, i, width) : fLeft;
            fRight = i < tmpList.size() ? calcFurtherRightPixel(fRight, i, width) : fRight;
        }

        int leftX = calcXFromIndex(fLeft, width);
        int rightX = calcXFromIndex(fRight, width);
        int topY = calcYFromIndex(root, width);
        int botY = calcYFromIndex(bottom, width);

        drawBorder(leftX, rightX, topY, botY);


    }

    public void drawBorder(int leftX, int rightX, int topY, int botY) {
        for(int x=leftX; x<=rightX; x++)
            pw.setColor(x, topY, Color.BLUE);
        for(int y=topY; y<=botY; y++)
            pw.setColor(rightX, y, Color.BLUE);
        for(int x=rightX; x>=rightX; x--)
            pw.setColor(x, botY, Color.BLUE);
        for(int y=botY; y>=topY; y--)
            pw.setColor(leftX, y, Color.BLUE);
    }

    public void setPixelBorders() {
        for(int i : fruitClusters.keySet())
            drawClusterBorder(i, fruitClusters, width);
        System.out.println("wahay");
    }

//    public void getClusterAtMouse(javafx.scene.input.MouseEvent mouseEvent) {
//        int x = (int)mouseEvent.getX(), y = (int)mouseEvent.getY();
//        while(gsImg!=null) {
//            if(pixelIsWhite(pixelArray, calculateArrayPosition(y, x))) {
//                int root = DisjointSet.find(pixelArray, calculateArrayPosition(y, x));
//                ArrayList<Integer> tmpList = fruitClusters.get(root);
//            }
//        }
//    }

    public int totalWhitePixels(HashMap<Integer, ArrayList<Integer>> hm) {
        int totalPixels=0;
        for (int i : hm.keySet()) {
            totalPixels += hm.get(i).size();
        }
        return totalPixels;
    }

    public void createHashMap(int[] array, HashMap<Integer, ArrayList<Integer>> hashMap) {
        for(int x=0; x<array.length; x++) {
            if(pixelIsWhite(array, x)) {
                int root = DisjointSet.find(array, x);
                if (rootNotStored(root, hashMap)) {
                    ArrayList<Integer> tmpList = new ArrayList<>();
                    for (int i = x; i < array.length; i++) {
                        if (pixelIsWhite(array, i) && currentRootEqualsTempRoot(array, i, root)) {
                            tmpList.add(i);
                        }
                    }
                    hashMap.put(root, tmpList);
                }
            }
        }
    }

    public void unionFruitPixels(int[] array, int width, int height) {
        for (int i=0; i<array.length; i++) {
            if (pixelIsWhite(array, i)) {
                int top = i - width, right = i + 1, bottom = i + width, left = i - 1;
                if (!atTopEdge(i, width)) {
                    checkTopPixel(array, i, top);
                }
                if (!atRightEdge(i, width)) {
                    checkRightPixel(array, i, right);
                }
                if (!atBottomEdge(i, width, height)) {
                    checkBottomPixel(array, i, bottom);
                }
                if (!atLeftEdge(i, width)) {
                    checkLeftPixel(array, i, left);
                }
            }
        }
    }

    public void unionPixels(int[] array, int a, int b) {
        if(DisjointSet.find(array, a) < DisjointSet.find(array, b))
            DisjointSet.quickUnion(array, a, b);
        DisjointSet.quickUnion(array, b, a);
    }

    public void checkTopPixel(int[] a, int i, int top) {
        if (pixelIsWhite(a, top)) {
            unionPixels(a, i, top);
        }
    }

    public void checkRightPixel(int[] a, int i, int right) {
        if (pixelIsWhite(a, right)) {
            unionPixels(a, i, right);
        }
    }

    public void checkBottomPixel(int[] a, int i, int bottom) {
        if (pixelIsWhite(a, bottom)) {
            unionPixels(a, i, bottom);
        }
    }

    public void checkLeftPixel(int[] a, int i, int left) {
        if (pixelIsWhite(a, left)) {
            unionPixels(a, i, left);
        }
    }

    public void biggerRedFruitRecog() {
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                pixelColor = pr.getColor(x, y);
                if (compareHSB() && pixelColor.getRed()>pixelColor.getBlue() && pixelColor.getRed()>pixelColor.getGreen()) {
                    pw.setColor(x, y, Color.valueOf("#ffffff"));
                    addFruitToArray(y, x);
                } else {
                    pw.setColor(x, y, Color.valueOf("#000000"));
                    addNonFruitToArray(y, x);
                }
            }
        }
    }

    public void biggerBlueFruitRecog() {
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

    public void addFruitToArray(int y, int x) {
        int pos = calculateArrayPosition(y, x);
        pixelArray[pos] = pos;
    }

    public void addNonFruitToArray(int y, int x) {
        pixelArray[calculateArrayPosition(y, x)] = -1;
    }

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
        gsImg = greyscaleConversion();
        greyImageView.setImage(gsImg);
    }

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

    public void fileChooser() throws FileNotFoundException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"), new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"));
        File file = fc.showOpenDialog(null);
        initializeImage(file);
        setDifferences(360, 0.4, 0.3);
        chosenImageView.setImage(img);
        pleaseClick.setVisible(true);
    }

    public void initializeImage(File file) throws FileNotFoundException {
        img = new Image(new FileInputStream(file), (int)chosenImageView.getFitWidth(), (int)chosenImageView.getFitHeight(), false, true);
        width = (int)img.getWidth();
        height = (int)img.getHeight();
        pr=img.getPixelReader();
        wi=new WritableImage(pr, width, height);
        pw=wi.getPixelWriter();
        pixelArray = new int[width*height];
    }

    public void initializeBlackWhiteImg() {
        gsImg=img;
        gsPr=gsImg.getPixelReader();
        gsWi=new WritableImage(gsPr, width, height);
        gsPw=gsWi.getPixelWriter();
    }

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

    public boolean pixelIsWhite(int[] a, int i) {
        return a[i] != -1;
    }

    public boolean atTopEdge(int i, int w) {
        return i-w<0;
    }

    public boolean atRightEdge(int i, int w) {
        return (2*(i+1)) % w == 0;
    }

    public boolean atBottomEdge(int i, int w, int h) {
        return i+w > w*h;
    }

    public boolean atLeftEdge(int i, int w) {
        return i%w==0;
    }

    public boolean rootNotStored(int root, HashMap<Integer, ArrayList<Integer>> hashMap) {
        return !hashMap.containsKey(root);
    }

    public boolean currentRootEqualsTempRoot(int[] array, int i, int root) {
        return DisjointSet.find(array, i) == root;
    }

    public int decideRGB(double r, double g, double b) {
        if (r > g && r > b) return 1;
        return 2;
    }

    public int calculateArrayPosition(int y, int x) {
        return (y*width) + x;
    }

    public int calcXFromIndex(int i, int width) {
        return (i+1)%width;
    }

    public int calcYFromIndex(int i, int width) {
        return (i+1)/width;
    }

    public Image greyscaleConversion() {
        initializeBlackWhiteImg();
        pixelArray = new int[width*height];
        if (decideRGB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue()) == 1) {
            biggerRedFruitRecog();
        }
        else if (decideRGB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue()) == 2) {
            biggerBlueFruitRecog();
        }
        unionFruitPixels(pixelArray, width, height);
        createHashMap(pixelArray, fruitClusters);
        displayHashMap();
        //displayArray();
        img=wi;
        return img;
    }

    public void displayHashMap() {
        Object[] a = fruitClusters.keySet().toArray();
        for (Object o : a) {
            System.out.println("Root: ");
            System.out.println(o);
            System.out.println("Pixels in Cluster:");
            System.out.println(fruitClusters.get(o));
            System.out.println("-----------------------------------------");
        }
    }

    public void displayArray() {
        for(int x=0; x<pixelArray.length; x++) {
            System.out.println(x + " " + pixelArray[x]);
        }
    }
}
