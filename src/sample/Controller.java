package sample;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.scene.control.*;
import javafx.scene.image.*;
import java.util.*;
import java.io.*;

public class Controller {
    @FXML ImageView chosenImageView, greyImageView;
    @FXML Slider hueSlider, saturationSlider, brightnessSlider;
    @FXML Label pleaseClick;

    Color pixelColor;
    Image img;
    public void fileChooser() throws FileNotFoundException {
        try {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"), new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"));
            File file = fc.showOpenDialog(null);
            initializeImage(file);
            setDifferences(360, 0.4, 0.3);
            chosenImageView.setImage(img);
            pleaseClick.setVisible(true);
        } catch (NullPointerException ignored) {}
    }

    int[] pixelArray;
    int width, height;
    PixelReader pr;PixelWriter pw; WritableImage wi;
    public void initializeImage(File file) throws FileNotFoundException {
        img = new Image(new FileInputStream(file), (int)chosenImageView.getFitWidth(), (int)chosenImageView.getFitHeight(), false, true);
        width = (int)img.getWidth();
        height = (int)img.getHeight();
        pr=img.getPixelReader();
        wi=new WritableImage(pr, width, height);
        pw=wi.getPixelWriter();
        pixelArray = new int[width*height];
    }

    double hueDifference, saturationDifference, brightnessDifference;
    public void setDifferences(double newHue, double newSaturation, double newBrightness) {
        hueDifference=newHue;
        saturationDifference=newSaturation;
        brightnessDifference=newBrightness;
    }

    Color selectedColor;
    double selectedHue, selectedSaturation, selectedBrightness;
    public void getColourAtMouseR(javafx.scene.input.MouseEvent mouseEvent) {
        try {
            if(chosenImageView!=null) {
                selectedColor = pr.getColor((int)mouseEvent.getX(), (int)mouseEvent.getY());
                setHSB();
                if (greyImageView.getImage() != null)
                    displayGreyImage();
            }
        } catch (Exception ignore) {}
    }

    public void setHSB() {
        selectedHue=selectedColor.getHue();
        selectedSaturation=selectedColor.getSaturation();
        selectedBrightness=selectedColor.getBrightness();
    }

    Image gsImg;
    public void displayGreyImage() {
        try {
            gsImg = greyscaleConversion();
            greyImageView.setImage(gsImg);
        } catch (Exception e) {
            Alert a = createAlert("Uh oh..", "Choose an image first!");
            a.show();
        }
    }

    HashMap<Integer, ArrayList<Integer>> fruitClusters = new HashMap<>();
    public Image greyscaleConversion() {
        initializeBlackWhiteImg();
        if (decideRGB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue()) == 1)
            biggerRedFruitRecog();
        else if (decideRGB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue()) == 2)
            biggerBlueFruitRecog();

        unionFruitPixels(pixelArray, width, height);
        createHashMap(pixelArray, fruitClusters);
        return gsWi;
    }

    public int decideRGB(double r, double g, double b) {
        if (r > g && r > b) return 1;
        return 2;
    }

    PixelReader gsPr; PixelWriter gsPw; WritableImage gsWi;
    public void initializeBlackWhiteImg() {
        gsImg=img;
        gsPr=gsImg.getPixelReader();
        gsWi=new WritableImage(gsPr, width, height);
        gsPw=gsWi.getPixelWriter();
    }

    public void biggerRedFruitRecog() {
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                pixelColor = gsPr.getColor(x, y);
                if (compareHSB() && pixelColor.getRed()>pixelColor.getBlue() && pixelColor.getRed()>pixelColor.getGreen()) {
                    gsPw.setColor(x, y, Color.valueOf("#ffffff"));
                    addFruitToArray(y, x);
                } else {
                    gsPw.setColor(x, y, Color.valueOf("#000000"));
                    addNonFruitToArray(y, x);
                }
            }
        }
    }

    public void biggerBlueFruitRecog() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixelColor = gsPr.getColor(x, y);
                if (compareHSB() && pixelColor.getRed() < pixelColor.getBlue() && pixelColor.getGreen() < pixelColor.getBlue()) {
                    gsPw.setColor(x, y, Color.valueOf("#ffffff"));
                    addFruitToArray(y, x);
                } else {
                    gsPw.setColor(x, y, Color.valueOf("#000000"));
                    addNonFruitToArray(y, x);
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
        for(int i : pixelArray) {
            System.out.println(i + " " + pixelArray[i]);
        }
    }

    public boolean compareHSB() {
        return compareSaturation(pixelColor.getSaturation(), selectedSaturation, saturationDifference) && compareBrightness(pixelColor.getBrightness(), selectedBrightness, brightnessDifference) && compareHue(pixelColor.getHue(), selectedHue, hueDifference);
    }

    public void addFruitToArray(int y, int x) {
        int pos = calculateArrayPosition(y, x, width);
        pixelArray[pos] = pos;
    }

    public void addNonFruitToArray(int y, int x) {
        pixelArray[calculateArrayPosition(y, x, width)] = -1;
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

    public boolean rootNotStored(int root, HashMap<Integer, ArrayList<Integer>> hashMap) {
        return !hashMap.containsKey(root);
    }

    public boolean currentRootEqualsTempRoot(int[] array, int i, int root) {
        return DisjointSet.find(array, i) == root;
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

    public int calculateArrayPosition(int y, int x, int width) {
        return (y*width) + x;
    }

    public void unionPixels(int[] array, int a, int b) {
        if(DisjointSet.find(array, a) < DisjointSet.find(array, b))
            DisjointSet.quickUnion(array, a, b);
        DisjointSet.quickUnion(array, b, a);
    }

    public void setPixelBorders() {
        try {
            removeOutliers(fruitClusters);
            for(int i : fruitClusters.keySet())
                drawClusterBorder(i, fruitClusters, width);
            chosenImageView.setImage(wi);
        }
        catch (Exception e) {
            Alert a = createAlert("Uh oh..", "Convert your image to black and white first!");
            a.show();
        }
    }

    public Alert createAlert(String title, String header) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(header);
        ImageView imageView = new ImageView(new Image(String.valueOf(getClass().getResource("/resources/warning.png"))));
        a.setGraphic(imageView);
        return a;
    }

    public ArrayList<Integer> createSortedSizeArray(HashMap<Integer, ArrayList<Integer>> hm) {
        ArrayList<Integer> arrayOfSizes = new ArrayList<>();
        for(int i : hm.keySet())
            arrayOfSizes.add(hm.get(i).size());
        Collections.sort(arrayOfSizes);
        return arrayOfSizes;
    }

    public int calcIQR(ArrayList<Integer> a, int length) {
        int middleIndex = calcMedian(0, length);
        int Q1 = a.get(calcMedian(0, middleIndex));
        int Q3 = a.get(calcMedian(middleIndex+1, length));
        return (Q3-Q1);
    }

    public int calcMedian(int l, int r) {
        int n = r-l+1;
        n = (n+1)/2-1;
        return n+l;
    }

    public void removeOutliers(HashMap<Integer, ArrayList<Integer>> hm) {
        int IQR = calcIQR(createSortedSizeArray(hm), createSortedSizeArray(hm).size());
        Set<Map.Entry<Integer, ArrayList<Integer>>> setOfEntries = hm.entrySet();
        Iterator<Map.Entry<Integer, ArrayList<Integer>>> iterator = setOfEntries.iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, ArrayList<Integer>> entry = iterator.next();
            int size = entry.getValue().size();
            if(size < IQR)
                iterator.remove();
        }
    }

    public void drawClusterBorder(int root, HashMap<Integer, ArrayList<Integer>> fruitClusters, int width) {
        List<Integer> tmpList = fruitClusters.get(root);
        int furthestLeftPixel = root,
            furthestRightPixel = root,
            bottomPixel = tmpList.get(tmpList.size()-1);
        for(int i : tmpList) {
            furthestLeftPixel = i>=tmpList.size() ? calcFurtherLeftPixel(furthestLeftPixel, i, width) : furthestLeftPixel;
            furthestRightPixel = i>=tmpList.size() ? calcFurtherRightPixel(furthestRightPixel, i, width) : furthestRightPixel;
        }
        int leftX = calcXFromIndex(furthestLeftPixel, width),
            rightX = calcXFromIndex(furthestRightPixel, width),
            topY = calcYFromIndex(root, width),
            botY = calcYFromIndex(bottomPixel, width);
        drawBorder(leftX, rightX, topY, botY);
    }

    public int calcFurtherLeftPixel(int posA, int posB, int w) {
        return posA%w < posB%w ? posA : posB;
    }

    public int calcFurtherRightPixel(int posA, int posB, int w) {
        return posA%w > posB%w ? posA : posB;
    }

    public int calcXFromIndex(int i, int width) {
        return (i)%width;
    }

    public int calcYFromIndex(int i, int width) {
        return (i)/width;
    }

    public void drawBorder(int leftX, int rightX, int topY, int botY) {
        for(int x=leftX; x<=rightX; x++)
            pw.setColor(x, topY, Color.BLUE);
        for(int y=topY; y<=botY; y++)
            pw.setColor(rightX, y, Color.BLUE);
        for(int x=rightX; x>=leftX; x--)
            pw.setColor(x, botY, Color.BLUE);
        for(int y=botY; y>=topY; y--)
            pw.setColor(leftX, y, Color.BLUE);
    }

    public void getClusterAtMouse(javafx.scene.input.MouseEvent event) {
        int x = (int)event.getX(), y = (int)event.getY();
        if(gsImg!=null)
            if(pixelIsWhite(pixelArray, calculateArrayPosition(y, x, width))) {
                int root = DisjointSet.find(pixelArray, calculateArrayPosition(y, x, width));
                Tooltip tooltip = new Tooltip();
                tooltip.setText("Fruit/Cluster number: " + "#1" + "\n" + "Estimated size (pixel units): " + fruitClusters.get(root).size());
                Tooltip.install(chosenImageView, tooltip);
            }
        pleaseClick.setVisible(false);
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

}
