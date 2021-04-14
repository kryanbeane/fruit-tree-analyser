package sample;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FruitImage {
    Image originalImage, editableImage;
    int width, height;
    PixelReader pr; PixelWriter pw; WritableImage wi;

    public FruitImage(FileInputStream userFile, int width, int height) {
        this.originalImage = new Image(userFile, width, height, false, true);
        this.editableImage = originalImage;
        this.width = (int)editableImage.getWidth();
        this.height = (int)editableImage.getHeight();
        this.pr = editableImage.getPixelReader();
        this.wi = new WritableImage(pr, width, height);
        this.pw = wi.getPixelWriter();
    }

    public void drawClusterBorder(int root, HashMap<Integer, ArrayList<Integer>> map) {
        List<Integer> tmpList = map.get(root);
        int furthestLeftPixel = root, furthestRightPixel = root, bottomPixel = tmpList.get(tmpList.size()-1);
        for(int i : tmpList) {
            furthestLeftPixel = i >= tmpList.size() ? calcFurtherLeftPixel(furthestLeftPixel, i) : furthestLeftPixel;
            furthestRightPixel = i >= tmpList.size() ? calcFurtherRightPixel(furthestRightPixel, i) : furthestRightPixel;
        }
        int leftX = calcXFromIndex(furthestLeftPixel),
                rightX = calcXFromIndex(furthestRightPixel),
                topY = calcYFromIndex(root),
                botY = calcYFromIndex(bottomPixel);
        drawBorder(leftX, rightX, topY, botY);
        editableImage=wi;
    }

    public int calcFurtherLeftPixel(int posA, int posB) {
        return posA%width < posB%width ? posA : posB;
    }

    public int calcFurtherRightPixel(int posA, int posB) {
        return posA%width > posB%width ? posA : posB;
    }

    public int calcXFromIndex(int i) {
        return (i)%width;
    }

    public int calcYFromIndex(int i) {
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

    public void resetEditableImage() {
        editableImage = originalImage;
        pr = editableImage.getPixelReader();
        wi = new WritableImage(pr, width, height);
        pw = wi.getPixelWriter();
    }

}
