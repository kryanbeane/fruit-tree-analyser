package sample;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../resources/main.fxml")));
        primaryStage.getIcons().add(new Image("/resources/grapes.png"));
        primaryStage.setScene(new Scene(root, 850, 650));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


}
