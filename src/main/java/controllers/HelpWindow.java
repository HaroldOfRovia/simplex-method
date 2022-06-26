package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HelpWindow {
    @FXML
    private Label helpLabel;
    private Stage stage;

    public void setAll(int code, Stage stage) {
        this.stage = stage;
        helpLabel.setWrapText(true);
        switch (code) {
            case (0):
                helpLabel.setText("�������� �����:\n" +
                        "   ��� ������������ �������� ������ ���������� ��������� ������, �������� � ��� ����������� ������� " +
                        "����������� � ������ ����������, ���� ������ ����� ������ ��� ������ ����������/�����������, " +
                        "�� �� ����� �������� �� ������ ���������� ������ ������. ��� ��, ��� ����� ����� ������� ����������� " +
                        "������ � ��������� ����������, ����� ��� ����� ������� �������� ����������. \n\n" +
                        "������������� �����: \n" +
                        "   ������� �� ������������ ����� ���� � ���, ��� ���� ��� ����� ������ ���������� �������� �������.\n\n" +
                        "����������� �������:\n" +
                        "   ��� ������������ ������� ���������� � ���� ������ ������ ������� ����������, ������� ����� �������� ����� ������.");
                break;
            case (1):
                helpLabel.setText("\">>\"\n" +
                        "   ����������� ��������� ��� �������������.\n\n" +
                        "\"<<\"\n" +
                        "   ���������� ���������� �������\n\n" +
                        "������ � �������:\n" +
                        "   ��������� ������� ��������� ������� �������. ���� ������� �������� ������, �� " +
                        "� ��� ��������� ��������� ������� �������, ���� ������� ��������� ���������� ����� ������� �� �������.\n" +
                        "   ���� � ������� ������ ������� �� ������ �������� ��������, �� ���� ��� ������� �����, ���� ������������� ����� " +
                        "�������� � ���������� ������ \">>\".\n\n" +
                        "�����:\n" +
                        "   ����� ������� ����� �����.\n" +
                        "   ����� ������� �������, �� ������� �������� ������� ������� ����� ���������.");
                break;
            case (2):
                helpLabel.setText("������ �����:\n" +
                        "   ���������� ������� ������ ��� �������, � ������� ���������� �������.\n\n" +
                        "\"-n\"\n" +
                        "   ����������� ������������ �������.");
                break;
            default:
                helpLabel.setText("How did you get here?");
                break;
        }
    }

    public void ok(ActionEvent actionEvent) {
        stage.close();
    }
}
