package sample;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.util.Duration;
import java.util.*;
import java.io.*;

public class Controller {
    @FXML ImageView chosenImageView;
    @FXML
    ImageView blackWhiteImageView;
    @FXML Slider hueSlider, saturationSlider, brightnessSlider;
    @FXML Label yourImage, bwVersion;
    @FXML StackPane stack, sizePane;
    @FXML RadioButton radio;
    @FXML Button bwBut;
    @FXML AnchorPane noMenu;
    @FXML HBox menu, recogSettings, colorSettings;
    Color fruitColor;
    FruitImage fruitImage;
    BlackWhiteImage blackWhiteImage;
    ClusterMap clusterMap;
    Boolean sizesAreShown=false;

    public void fileChooser() throws FileNotFoundException {
        try {
            resetImageGUI();
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"), new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"));
            File file = fc.showOpenDialog(null);
            fruitImage = new FruitImage(new FileInputStream(file), (int)chosenImageView.getFitWidth(), (int)chosenImageView.getFitHeight());
            chosenImageView.setImage(fruitImage.originalImage);
            yourImage.setVisible(true);
            createTooltip(bwBut, "Try clicking different areas of the" + "\n fruit to get better conversions!");
        } catch (NullPointerException ignored) {}
    }

    public void createTooltip(Node placement, String text) {
        Tooltip tooltip = new Tooltip();
        tooltip.setText(text);
        tooltip.setShowDelay(Duration.millis(0));
        tooltip.setHideDelay(Duration.millis(0));
        Tooltip.install(placement, tooltip);
    }

    public void resetImageGUI() {
        minimizeMenu();
        bwVersion.setVisible(false);
        yourImage.setVisible(false);
        chosenImageView.setImage(null);
        blackWhiteImageView.setImage(null);
    }

    public void getColourAtMouse(javafx.scene.input.MouseEvent mouseEvent) {
        try {
            if (chosenImageView!=null) {
                fruitColor = fruitImage.pr.getColor((int)mouseEvent.getX(), (int)mouseEvent.getY());
                if (blackWhiteImageView.getImage() != null)
                    displayBlackWhiteImage();
            }
        } catch (Exception ignore) {}
    }

    public void getClusterAtMouse(javafx.scene.input.MouseEvent event) {
        try {
            int x=(int)event.getX(), y=(int)event.getY();
            if(blackWhiteImage!=null)
                if(blackWhiteImage.fruitArray.pixelIsWhite(blackWhiteImage.fruitArray.calculateArrayPosition(y, x))) {
                    int root = blackWhiteImage.fruitArray.find(blackWhiteImage.fruitArray.array, blackWhiteImage.fruitArray.calculateArrayPosition(y, x));
                    Tooltip tooltip = new Tooltip();
                    int rank = clusterMap.getClusterSizeRank(root);
                    tooltip.setText("Fruit/Cluster number: " + rank + "\n" + "Estimated size (pixel units): " + clusterMap.map.get(root).size());
                    if(sizesAreShown)
                        Tooltip.install(sizePane, tooltip);
                    else Tooltip.install(chosenImageView, tooltip);
                }
        } catch (Exception ignore) {}
    }

    public void displayBlackWhiteImage() {
        try {
            initialiseBlackWhiteImage();
            blackWhiteImageView.setImage(blackWhiteImage.bwImage);
            bwVersion.setVisible(true);
        }
        catch (Exception e) {
            Alert error;
            if (fruitImage==null) error = createAlert("Uh oh..", "Choose an image first!");
            else if (fruitColor==null) error =createAlert("Uh oh..", "Click a fruit first!");
            else error = createAlert("Uh oh..", e.getCause().toString());
            error.show();
        }
    }

    public void initialiseBlackWhiteImage() {
        blackWhiteImage = new BlackWhiteImage(fruitImage.editableImage, fruitColor, fruitImage.width, fruitImage.height);
    }

    public Alert createAlert(String title, String header) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(header);
        ImageView imageView = new ImageView(new Image(String.valueOf(getClass().getResource("/resources/warning.png"))));
        a.setGraphic(imageView);
        return a;
    }

    public void showOnScreenSizes() {
        if(radio.isSelected()) {
            sizePane.toFront();
            sizesAreShown=true;
        } else {
            sizePane.toBack();
            sizesAreShown=false;
        }
    }

    public void createSizePane() {
        for (int i : clusterMap.map.keySet()) {
            Font font = Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 10);
            Label label = new Label(String.valueOf(clusterMap.getClusterSizeRank(i)));
            label.setTextFill(Color.WHITE);
            label.setFont(font);
            sizePane.getChildren().add(label);
            label.setTranslateX(fruitImage.calcXFromIndex(i) - (fruitImage.width >> 1)+3);
            label.setTranslateY(fruitImage.calcYFromIndex(i) - (fruitImage.width >> 1)+3);
        }
    }

    public void setPixelBorders() {
        try {
            fruitImage.resetEditableImage();
            clusterMap = new ClusterMap();
            clusterMap.createHashMap(blackWhiteImage.fruitArray);
            if(clusterMap.map.size()>3)
                clusterMap.removeOutliers();
            for(int i : clusterMap.map.keySet())
                fruitImage.drawClusterBorder(i, clusterMap.map);
            chosenImageView.setImage(fruitImage.editableImage);
            createSizePane();
        }
        catch (Exception e) {
            Alert a=createAlert("", "");
            if(chosenImageView.getImage()==null)
                a=createAlert("Uh oh..", "Choose an image first!");
            else if(blackWhiteImage == null)
                a=createAlert("Uh oh..", "Convert image to black and white first!");
            a.show();
        }
    }

    public void displayMenu() {
        menu.setVisible(true);
        noMenu.setVisible(false);
    }

    public void minimizeMenu() {
        menu.setVisible(false);
        noMenu.setVisible(true);
        recogSettings.setVisible(false);
        colorSettings.setVisible(false);
    }

    public void displayRecogMenu() {
        recogSettings.setVisible(true);
        colorSettings.setVisible(false);
    }

    public void displayColorMenu() {
        recogSettings.setVisible(false);
        colorSettings.setVisible(true);
    }

    public void editImagePixels(ColorAdjust colorAdjust) {
        for(int y=0; y<fruitImage.width; y++)
            for(int x=0; x<fruitImage.width; x++) {
                Color c = fruitImage.pr.getColor(x, y);
                c = c.deriveColor(colorAdjust.getHue(), colorAdjust.getSaturation(), colorAdjust.getBrightness(), 1);
                fruitImage.pw.setColor(x, y, c);
            }
    }

    public void sliderAdjustments() {
        try {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(hueSlider.getValue());
            colorAdjust.setSaturation(saturationSlider.getValue());
            colorAdjust.setBrightness(brightnessSlider.getValue());
            editImagePixels(colorAdjust);
            fruitImage.setEditableImage(fruitImage.wi);
            chosenImageView.setImage(fruitImage.editableImage);
            displayBlackWhiteImage();
        } catch (Exception e) {
            createAlert("Uh oh..", "Try converting an image first!");
        }
    }
}
