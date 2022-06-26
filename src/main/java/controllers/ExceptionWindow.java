package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.*;

public class ExceptionWindow {
    @FXML
    private Label errLabel;
    private Stage stage;

    public void setEx(Exception err, Stage stage) {
        this.stage = stage;
        errLabel.setWrapText(true);
        errLabel.setText(err.getMessage());
    }

    public void ok(ActionEvent actionEvent) {
        stage.close();
    }
}
