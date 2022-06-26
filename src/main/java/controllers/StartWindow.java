package controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.Chart;
import logic.RationalNumber;
import logic.Simplex;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class StartWindow implements Initializable {
    @FXML
    private Label countX;
    @FXML
    private Label countEquation;
    @FXML
    private HBox gridPanePlaceFun;
    @FXML
    private HBox gridPanePlaceEqu;
    @FXML
    private VBox startPaneControl;
    @FXML
    private HBox simplexPaneControl;
    @FXML
    private VBox countControlPane;
    @FXML
    private VBox currentFunction;
    @FXML
    private HBox answerPlace;
    private GridPane gridTable;
    private GridPane gridFunction;
    private ArrayList<CheckBox> selectBasis;
    private ArrayList<TextField> functionInputs;
    private ArrayList<ArrayList<TextField>> tableInputs;
    private ArrayList<Simplex> simplexHistory;
    private int widthCell = 42;
    private int helpCode;

    private void alertWindow(Exception ex) throws Exception {
        Stage alertStage = new Stage();
        alertStage.setTitle("Attention!");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExceptionWindow.fxml"));
        Parent view = loader.load();

        ExceptionWindow exceptionController = loader.getController();
        exceptionController.setEx(ex, alertStage);

        Scene scene = new Scene(view);
        alertStage.setResizable(false);
        alertStage.setScene(scene);
        alertStage.show();
    }

    public void helpWindow() throws Exception {
        Stage helpStage = new Stage();
        helpStage.setTitle("Справка");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HelpWindow.fxml"));
        Parent view = loader.load();

        HelpWindow helpController = loader.getController();
        helpController.setAll(helpCode, helpStage);

        Scene scene = new Scene(view);
        helpStage.setResizable(false);
        helpStage.setScene(scene);
        helpStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridFunction = new GridPane();
        gridFunction.setHgap(2);
        gridFunction.setVgap(2);
        gridFunction.setPadding(new Insets(5, 0, 5, 0));
        functionInputs = new ArrayList<>();
        selectBasis = new ArrayList<>();
        gridTable = new GridPane();
        tableInputs = new ArrayList<>();
        simplexHistory = new ArrayList<>();
        gridTable.setHgap(2);
        gridTable.setVgap(2);
        clearAll();
    }

    public void buildStartTable() {
        gridTable.getChildren().clear();
        for (int i = 0; i <= tableInputs.size(); i++) {
            for (int j = 0; j < tableInputs.get(0).size(); j++) {
                if (i == 0) {
                    Label v = new Label();
                    int a = j;
                    if (a == tableInputs.get(0).size() - 1) {
                        a++;
                        v.setText("a");
                    } else
                        v.setText("x" + (a + 1));
                    v.setAlignment(Pos.CENTER);
                    GridPane.setHalignment(v, HPos.CENTER);
                    gridTable.add(v, a, 0);
                } else if (j == tableInputs.get(0).size() - 1) {
                    gridTable.add(new Label("="), j, i);
                    gridTable.add(tableInputs.get(i - 1).get(j), j + 1, i);
                } else {
                    gridTable.add(tableInputs.get(i - 1).get(j), j, i);
                }
            }
        }
    }

    public void clearAll() {
        helpCode = 0;
        countX.setText("2");
        countEquation.setText("1");
        answerPlace.getChildren().clear();
        simplexHistory.clear();
        currentFunction.getChildren().clear();
        countControlPane.setVisible(true);
        startPaneControl.setVisible(true);
        simplexPaneControl.setVisible(false);
        gridFunction.getChildren().clear();
        functionInputs.clear();
        selectBasis.clear();
        for (int i = 0; i < 3; i++) {
            TextField inputField = new TextField("0");
            inputField.setPrefWidth(widthCell);

            if (i == 0) {
                Label variable = new Label();
                variable.setText("a");
                variable.setAlignment(Pos.CENTER);
                GridPane.setHalignment(variable, HPos.CENTER);
                gridFunction.add(variable, i, 0);
            } else {
                CheckBox variable = new CheckBox();
                variable.setText("x" + i);
                variable.setAlignment(Pos.CENTER);
                GridPane.setHalignment(variable, HPos.CENTER);
                gridFunction.add(variable, i, 0);
                selectBasis.add(variable);
            }

            gridFunction.add(inputField, i, 1);
            functionInputs.add(inputField);
        }
        gridPanePlaceFun.setMaxWidth(functionInputs.size() * (widthCell + 2));
        gridPanePlaceFun.getChildren().clear();
        gridPanePlaceFun.getChildren().add(0, gridFunction);


        gridTable.getChildren().clear();
        tableInputs.clear();
        tableInputs.add(new ArrayList<TextField>());
        for (int i = 0; i < gridFunction.getColumnCount(); i++) {
            TextField inputField = new TextField("0");
            inputField.setPrefWidth(widthCell);
            tableInputs.get(0).add(inputField);
        }
        buildStartTable();
        gridPanePlaceEqu.getChildren().clear();
        gridPanePlaceEqu.getChildren().add(gridTable);
    }

    public void drawGridFunc() {
        gridFunction.getChildren().clear();

        for (int i = 0; i < functionInputs.size(); i++) {
            if (i == 0) {
                Label variable = new Label();
                variable.setText("a");
                variable.setAlignment(Pos.CENTER);
                GridPane.setHalignment(variable, HPos.CENTER);
                gridFunction.add(variable, i, 0);
            } else {
                CheckBox variable = selectBasis.get(i - 1);
                gridFunction.add(variable, i, 0);
            }

            gridFunction.add(functionInputs.get(i), i, 1);
        }
        gridPanePlaceFun.setMaxWidth(functionInputs.size() * (widthCell + 2));
        buildStartTable();
    }

    public void addX(ActionEvent actionEvent) {
        int numX = gridFunction.getColumnCount();
        if (numX >= 17)
            return;
        TextField inputFunctionField = new TextField("0");
        inputFunctionField.setPrefWidth(widthCell);

        CheckBox variable = new CheckBox();
        variable.setText("x" + numX);
        variable.setAlignment(Pos.CENTER);

        GridPane.setHalignment(variable, HPos.CENTER);
        gridFunction.add(variable, numX, 0);
        selectBasis.add(variable);
        gridFunction.add(inputFunctionField, numX, 1);
        functionInputs.add(inputFunctionField);
        gridPanePlaceFun.setMaxWidth(functionInputs.size() * (widthCell + 2));

        countX.setText(Integer.toString(numX));


        for (int i = 0; i < tableInputs.size(); i++) {
            TextField newField = new TextField("0");
            newField.setPrefWidth(widthCell);
            tableInputs.get(i).add(tableInputs.get(0).size() - 1, newField);
        }
        buildStartTable();
    }

    public void deleteX(ActionEvent actionEvent) {
        int numX = gridFunction.getColumnCount() - 1;
        if (numX <= 2)
            return;
        gridFunction.getChildren().remove(numX * 2 + 1);
        gridFunction.getChildren().remove(numX * 2);
        functionInputs.remove(functionInputs.size() - 1);
        gridPanePlaceFun.setMaxWidth(functionInputs.size() * (widthCell + 2));

        countX.setText(Integer.toString(numX - 1));


        for (int i = 0; i < tableInputs.size(); i++) {
            tableInputs.get(i).remove(tableInputs.get(0).size() - 1);
        }
        buildStartTable();
    }

    public void addEquation(ActionEvent actionEvent) {
        int countEq = gridTable.getRowCount() - 1;
        if (countEq >= 16)
            return;

        ArrayList<TextField> tmp = new ArrayList<>();
        for (int i = 0; i < Integer.parseInt(countX.getText()) + 1; i++) {
            TextField newField = new TextField("0");
            newField.setPrefWidth(widthCell);
            tmp.add(newField);
        }
        tableInputs.add(tmp);
        buildStartTable();

        countEquation.setText(Integer.toString(countEq + 1));
    }

    public void deleteEquation(ActionEvent actionEvent) {
        int countEq = gridTable.getRowCount() - 1;
        if (countEq <= 1)
            return;

        tableInputs.remove(tableInputs.size() - 1);
        buildStartTable();

        countEquation.setText(Integer.toString(countEq - 1));
    }

    private Simplex fillTask() throws Exception {

        Simplex task = new Simplex();

        boolean funcEmpty = true;
        for (TextField functionInput : functionInputs) {
            task.function.add(new RationalNumber(functionInput.getText()));
            if (!new RationalNumber(functionInput.getText()).equals(new RationalNumber(0)))
                funcEmpty = false;
        }
        if (funcEmpty)
            throw new Exception("Function is empty!");

        for (int i = 0; i < selectBasis.size(); i++) {
            if (selectBasis.get(i).isSelected())
                task.basis.add(i + 1);
        }
        for (int i = 0; i < tableInputs.size(); i++) {
            boolean skip = true;
            for (int j = 0; j < tableInputs.get(0).size(); j++) {
                if (!new RationalNumber(tableInputs.get(i).get(j).getText()).equals(new RationalNumber(0))) {
                    skip = false;
                    break;
                }
            }
            if (skip)
                continue;
            task.table.add(new ArrayList<RationalNumber>());
            for (int j = 0; j < tableInputs.get(0).size(); j++) {
                task.table.get(i).add(new RationalNumber(tableInputs.get(i).get(j).getText()));
            }
        }
        if (task.table.isEmpty())
            throw new Exception("All limitations is empty!");
        task.makeFreeVariables();

        if (task.basis.size() + task.freeVariables.size() > task.function.size() - 1)
            task.isArtificial = true;

        return task;
    }

    public void getTask(ActionEvent actionEvent) throws Exception {
        try {
            Stage stageFileChooser = new Stage();
            stageFileChooser.setTitle("Select the task file");
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Only txt files", "*.txt"));
            fileChooser.setInitialDirectory(new File("./saves"));

            File file = fileChooser.showOpenDialog(stageFileChooser);
            if (file == null)
                return;

            clearAll();
            selectBasis = new ArrayList<CheckBox>();
            functionInputs = new ArrayList<TextField>();
            tableInputs = new ArrayList<ArrayList<TextField>>();

            Scanner sc = new Scanner(file);

            for (int i = 0; sc.hasNextLine(); i++) {
                String[] numbers;
                if (sc.hasNextLine()) {
                    countEquation.setText(Integer.toString(i - 1));
                }
                switch (i) {
                    case (0):
                        numbers = sc.nextLine().split(" ");
                        for (String str : numbers) {
                            TextField tmp = new TextField(str);
                            tmp.setPrefWidth(widthCell);
                            functionInputs.add(tmp);
                        }
                        break;
                    case (1):
                        numbers = sc.nextLine().split(" ");
                        for (int a = 1; a < numbers.length + 1; a++) {
                            CheckBox tmp = new CheckBox();
                            tmp.setSelected(Boolean.parseBoolean(numbers[a - 1]));
                            tmp.setText("x" + a);
                            tmp.setAlignment(Pos.CENTER);
                            GridPane.setHalignment(tmp, HPos.CENTER);
                            selectBasis.add(tmp);
                        }
                        break;
                    default:
                        numbers = sc.nextLine().split(" ");
                        countX.setText(Integer.toString(numbers.length - 1));
                        tableInputs.add(new ArrayList<TextField>());
                        for (String str : numbers) {
                            TextField tmp = new TextField(str);
                            tmp.setPrefWidth(widthCell);
                            tableInputs.get(i - 2).add(tmp);
                        }
                        break;
                }

            }

            drawGridFunc();
        } catch (Exception ex) {
            alertWindow(ex);
        }
    }

    public void saveTask(ActionEvent actionEvent) throws Exception {
        String path = "";
        PrintWriter writer = null;
        try {

            Stage stageFileChooser = new Stage();
            stageFileChooser.setTitle("Select the directory");
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setInitialDirectory(new File("./saves"));

            File dir = dirChooser.showDialog(stageFileChooser);
            if (dir == null)
                return;

            String fileName = "task.txt";
            for (int i = 1; ; i++) {
                File f = new File(dir.getPath() + "/" + fileName);
                if (!f.exists())
                    break;
                fileName = "task(" + i + ").txt";
            }
            writer = new PrintWriter(dir.getPath() + "/" + fileName, StandardCharsets.UTF_8);
            path = dir.getPath() + "/" + fileName;

            Simplex task = fillTask();
            for (int i = 0; i < task.function.size(); i++) {
                writer.print(task.function.get(i) + " ");
            }
            writer.println();

            for (int i = 0; i < selectBasis.size(); i++) {
                writer.print(selectBasis.get(i).isSelected() + " ");
            }

            for (int i = 0; i < tableInputs.size(); i++) {
                writer.println();
                for (int j = 0; j < tableInputs.get(0).size(); j++) {
                    writer.print(task.table.get(i).get(j) + " ");
                }
            }

            writer.close();
        } catch (Exception ex) {
            if (writer != null) {
                writer.close();
                Files.delete(Paths.get(path));
            }
            alertWindow(ex);
        }
    }

    public void switchViewNumbers(ActionEvent actionEvent) throws Exception {
        try {
            if (simplexHistory.isEmpty())
                return;
            RationalNumber.decimal = !RationalNumber.decimal;
            widthCell = widthCell == 42 ? 60 : 42;
            buildSimplexTable();
        } catch (Exception ex) {
            alertWindow(ex);
        }
    }

    private Label labelForTable(String str) {
        Label label = new Label(str);
        label.setPrefWidth(widthCell);
        label.setAlignment(Pos.CENTER);
        label.setPrefHeight(26);
        label.setPrefWidth(widthCell);
        label.setStyle("-fx-border-color: lightgray;" +
                "-fx-border-width: 1 1 1 1");
        return label;
    }

    public GridPane buildSimplexGridPane(Simplex task) throws Exception {
        try {
            GridPane table = new GridPane();
            table.setVgap(2);
            table.setHgap(2);
            ArrayList<ArrayList<Boolean>> buttonPosition = task.buttonPosition();

            int numberTable = 0;
            if (task.isArtificial)
                numberTable = simplexHistory.size() - 1;
            else {
                for (int i = 0; simplexHistory.get(i).isArtificial; i++)
                    numberTable++;
                numberTable = simplexHistory.size() - 1 - numberTable;
            }
            Label numberLabel;
            if (task.isArtificial)
                numberLabel = labelForTable("(" + numberTable + ")*");
            else
                numberLabel = labelForTable("(" + numberTable + ")");
            table.add(numberLabel, 0, 0);
            for (int i = 0; i < task.freeVariables.size() + 1; i++) {
                Label tmp = labelForTable("");
                if (i == task.freeVariables.size()) {
                    tmp.setText("B");
                } else {
                    tmp.setText("x" + task.freeVariables.get(i));
                }
                table.add(tmp, i + 1, 0);
            }

            for (int i = 0; i < task.basis.size() + 1; i++) {
                Label tmp = labelForTable("");
                if (i == task.basis.size()) {
                    tmp.setText("P");
                } else {
                    tmp.setText("x" + task.basis.get(i));
                }
                table.add(tmp, 0, i + 1);
            }

            for (int i = 0; i < task.table.size(); i++) {
                for (int j = 0; j < task.table.get(0).size(); j++) {
                    if (buttonPosition.get(i).get(j)) {
                        Button button = new Button(task.table.get(i).get(j).toString());
                        button.setPrefWidth(widthCell);
                        int finalI = i, finalJ = j;
                        button.setOnAction(event -> {
                            try {
                                nextStep(finalI, finalJ);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        table.add(button, j + 1, i + 1);
                    } else {
                        Label label = labelForTable(task.table.get(i).get(j).toString());
                        table.add(label, j + 1, i + 1);
                    }
                }
            }
            return table;
        } catch (Exception ex) {
            alertWindow(ex);
        }
        return null;
    }

    private void buildSimplexTable() throws Exception {
        try {
            gridPanePlaceEqu.getChildren().clear();
            gridPanePlaceEqu.getChildren().add(buildSimplexGridPane(simplexHistory.get(simplexHistory.size() - 1)));
        } catch (Exception ex) {
            alertWindow(ex);
        }
    }

    private Label getStartFunc() throws Exception {
        try {
            Label func = new Label();
            func.setAlignment(Pos.CENTER);
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < functionInputs.size(); i++) {
                if (new RationalNumber(functionInputs.get(i).getText()).equals(new RationalNumber(0)))
                    continue;
                if (i == 0)
                    str.append(functionInputs.get(i).getText());
                else {
                    if ((new RationalNumber(functionInputs.get(i).getText()).less(new RationalNumber(0))))
                        str.append(" ").append(functionInputs.get(i).getText()).append("x").append(i);
                    else
                        str.append(" +").append(functionInputs.get(i).getText()).append("x").append(i);
                }
            }
            if (str.toString().equals(""))
                str.append("0");
            func.setText(str.toString());
            return func;
        } catch (Exception ex) {
            alertWindow(ex);
        }
        return null;
    }

    public void startSolution(ActionEvent actionEvent) throws Exception {
        try {
            Simplex task = fillTask();
            task.buildTable();
            if (task.basis.size() + task.freeVariables.size() == task.function.size() - 1) {
                if (task.basis.size() != task.table.size() - 1) {
                    throw new Exception("There should be as many basic variables as there are equations!");
                }
            }
            helpCode = 1;
            simplexHistory.clear();
            simplexHistory.add(task);
            startPaneControl.setVisible(false);
            countControlPane.setVisible(false);
            simplexPaneControl.setVisible(true);
            gridPanePlaceFun.getChildren().clear();
            gridPanePlaceFun.getChildren().add(getStartFunc());
            Label currFunc = new Label(task.functionToString());
            currFunc.setAlignment(Pos.CENTER);
            currentFunction.getChildren().add(currFunc);
            buildSimplexTable();
        } catch (Exception ex) {
            alertWindow(ex);
        }
    }

    public void previousStep(ActionEvent actionEvent) throws Exception {
        if (simplexHistory.size() == 1) {
            helpCode = 0;
            simplexHistory.clear();
            gridPanePlaceEqu.getChildren().clear();
            buildStartTable();
            gridPanePlaceEqu.getChildren().add(gridTable);
            gridPanePlaceFun.getChildren().clear();
            drawGridFunc();
            gridPanePlaceFun.getChildren().add(gridFunction);
            simplexPaneControl.setVisible(false);
            startPaneControl.setVisible(true);
            currentFunction.getChildren().clear();
            countControlPane.setVisible(true);
            return;
        }
        answerPlace.getChildren().clear();
        simplexHistory.remove(simplexHistory.size() - 1);
        currentFunction.getChildren().clear();
        Label currFunc = new Label(simplexHistory.get(simplexHistory.size() - 1).functionToString());
        currFunc.setAlignment(Pos.CENTER);
        currentFunction.getChildren().add(currFunc);
        buildSimplexTable();
    }

    public void nextStepAny() throws Exception {
        try {
            nextStep(-1, -1);
        } catch (Exception ex) {
            alertWindow(ex);
        }
    }

    private void nextStep(int row, int column) throws Exception {
        try {
            Simplex task = simplexHistory.get(simplexHistory.size() - 1).nextStep(column, row);
            if (task.isSolved() && !task.isArtificial) {
                answerPlace.getChildren().add(task.getAnswer());
            }
            gridPanePlaceEqu.getChildren().clear();
            simplexHistory.add(task);
            gridPanePlaceEqu.getChildren().add(buildSimplexGridPane(task));
            currentFunction.getChildren().clear();
            Label currFunc = new Label(task.functionToString());
            currFunc.setAlignment(Pos.CENTER);
            currentFunction.getChildren().add(currFunc);
        } catch (Exception ex) {
            alertWindow(ex);
            if (answerPlace.getChildren().isEmpty())
                answerPlace.getChildren().add(simplexHistory.get(simplexHistory.size() - 1).getAnswer());
        }
    }

    public void fastAnswer(ActionEvent actionEvent) throws Exception {
        try {
            Simplex task = fillTask();
            task.buildTable();
            helpCode = 1;
            simplexHistory.add(task);
            startPaneControl.setVisible(false);
            countControlPane.setVisible(false);
            simplexPaneControl.setVisible(true);
            gridPanePlaceFun.getChildren().clear();
            gridPanePlaceFun.getChildren().add(getStartFunc());
            for (int i = 0; ; i++) {
                Simplex tmp = simplexHistory.get(i).clone();
                tmp = tmp.nextStep(-1, -1);
                simplexHistory.add(tmp);
            }
        } catch (Exception ex) {
            alertWindow(ex);
            Label currFunc = new Label(simplexHistory.get(simplexHistory.size() - 1).functionToString());
            currFunc.setAlignment(Pos.CENTER);
            currentFunction.getChildren().add(currFunc);
            buildSimplexTable();
            if (simplexHistory.get(simplexHistory.size() - 1).isSolved())
                answerPlace.getChildren().add(simplexHistory.get(simplexHistory.size() - 1).getAnswer());
        }
    }

    public void startChart(ActionEvent actionEvent) throws Exception {
        try {
            Simplex task = fillTask();

            Chart chart = new Chart(task);
            gridPanePlaceFun.getChildren().clear();
            Label func = new Label(task.functionToString());
            func.setAlignment(Pos.CENTER);
            gridPanePlaceFun.getChildren().add(func);

            Label rebuildFunc = new Label(chart.newFunctionToString());
            rebuildFunc.setAlignment(Pos.CENTER);
            currentFunction.getChildren().add(rebuildFunc);

            gridPanePlaceEqu.getChildren().clear();
            startPaneControl.setVisible(false);

            LineChart<Number, Number> lineChart = chart.getChart();
            gridPanePlaceEqu.getChildren().add(lineChart);
            helpCode = 2;
            countControlPane.setVisible(false);
            try {
                task.toArtificial();
                task.buildTable();
                simplexHistory.add(task);
                while (true) {
                    simplexHistory.add(simplexHistory.get(simplexHistory.size() - 1).nextStep(-1, -1));
                }
            } catch (Exception ex) {
                if (simplexHistory.isEmpty())
                    throw ex;
                else
                    answerPlace.getChildren().add(simplexHistory.get(simplexHistory.size() - 1).getAnswer());
            }

        } catch (Exception ex) {
            alertWindow(ex);
        }
    }
}
