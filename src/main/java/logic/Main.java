package logic;

import controllers.StartWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Task optimization");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StartWindow.fxml"));
        Parent mainView = loader.load();

        StartWindow mainController = loader.getController();

        Scene scene = new Scene(mainView);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
