package logic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class Chart {
    public ArrayList<RationalNumber> function;
    public ArrayList<ArrayList<RationalNumber>> table;
    public ArrayList<Integer> basis;
    public Simplex simplex;
    public ArrayList<ArrayList<RationalNumber>> points;
    public ArrayList<ArrayList<RationalNumber>> vertexes;

    public Chart(Simplex simplex) throws Exception {
        Simplex task = simplex.clone();
        if (task.function.size() != 3)
            task.gauss();
        if (task.freeVariables.size() != 2) {
            throw new Exception("With this chosen variables,\n" +
                    "it is impossible to express everything through 2 variables");
        }
        this.simplex = task;
        function = (ArrayList<RationalNumber>) task.function.clone();
        table = new ArrayList<>();
        if (task.function.size() != 3)
            for (int i = 0; i < task.table.size() - 1; i++) {
                table.add((ArrayList<RationalNumber>) task.table.get(i).clone());
            }
        else
            for (int i = 0; i < task.table.size(); i++) {
                table.add((ArrayList<RationalNumber>) task.table.get(i).clone());
            }
        this.basis = (ArrayList<Integer>) task.basis.clone();
        points = new ArrayList<>();
        findAllPoints();
        findVertexes();
    }

    public ArrayList<RationalNumber> findPoint(ArrayList<ArrayList<RationalNumber>> matrix) throws Exception {

        int curStr = 0;
        ArrayList<Integer> finStr = new ArrayList<>();

        for (int i = 0; i < 2; i++) {//метод гаусса
            if (matrix.get(curStr).get(i).isZero()) {//если текущий элемент 0, то ищем не 0
                for (int j = 0; j < matrix.size(); j++) {
                    if (finStr.contains(j))
                        continue;
                    if (!matrix.get(j).get(i).isZero()) {
                        ArrayList<RationalNumber> tmp = matrix.get(curStr);
                        matrix.set(curStr, (ArrayList<RationalNumber>) matrix.get(j).clone());
                        matrix.set(j, (ArrayList<RationalNumber>) tmp.clone());
                        break;
                    }
                }
                if (matrix.get(curStr).get(i).isZero()) {
                    return null;
                }
            }

            RationalNumber ratio = matrix.get(curStr).get(i).reverse();
            for (int j = 0; j < matrix.get(i).size(); j++) {//текущий элемент делаем единицей
                matrix.get(curStr).set(j, matrix.get(curStr).get(j).multiply(ratio));
            }
            for (int j = 0; j < matrix.size(); j++) {//идем по строке, чтоб вычесть из нее
                if (j == curStr)
                    continue;
                ratio = matrix.get(j).get(i).clone();
                for (int a = 0; a < matrix.get(j).size(); a++) {
                    matrix.get(j).set(a, matrix.get(j).get(a).subtraction(ratio.multiply(matrix.get(curStr).get(a))));
                }
            }
            finStr.add(curStr++);
        }

        return new ArrayList<RationalNumber>(List.of(matrix.get(0).get(2), matrix.get(1).get(2)));
    }

    public void findAllPoints() throws Exception {
        ArrayList<ArrayList<RationalNumber>> lines = new ArrayList<>();
        lines.add(new ArrayList<>(List.of(new RationalNumber(1), new RationalNumber(0), new RationalNumber(0))));
        lines.add(new ArrayList<>(List.of(new RationalNumber(0), new RationalNumber(1), new RationalNumber(0))));

        lines.addAll(table);

        for (int i = 0; i < lines.size() - 1; i++) {
            for (int j = i + 1; j < lines.size(); j++) {
                if (i == 0 && j == 1)
                    continue;
                ArrayList<ArrayList<RationalNumber>> tmp = new ArrayList<>();
                tmp.add((ArrayList<RationalNumber>) lines.get(i).clone());
                tmp.add((ArrayList<RationalNumber>) lines.get(j).clone());
                ArrayList<RationalNumber> a = findPoint(tmp);
                if (a == null || a.get(0).less(new RationalNumber(0)) || a.get(1).less(new RationalNumber(0)))
                    continue;
                points.add(a);
            }
        }
    }

    public void findVertexes() throws Exception {
        ArrayList<ArrayList<RationalNumber>> list = new ArrayList(points);
        list.add(new ArrayList<>(List.of(new RationalNumber(0), new RationalNumber(0))));

        vertexes = new ArrayList<>();
        for (ArrayList<RationalNumber> point : list) {
            boolean isVertexes = true;
            for (ArrayList<RationalNumber> equation : table) {
                if (point.get(0).multiply(equation.get(0)).sum(point.get(1).multiply(equation.get(1))).more(equation.get(2)))
                    isVertexes = false;
            }
            if (isVertexes)
                vertexes.add((ArrayList<RationalNumber>) point.clone());
        }

        sortVertexes();
    }

    public void sortVertexes() {
        ArrayList<ArrayList<RationalNumber>> sorted = new ArrayList<>(List.of(vertexes.get(0)));
    }

    public LineChart<Number, Number> getChart() throws Exception {
        NumberAxis x = new NumberAxis();
        x.setLabel("x" + simplex.freeVariables.get(0));
        NumberAxis y = new NumberAxis();
        y.setLabel("x" + simplex.freeVariables.get(1));
        LineChart<Number, Number> numberLineChart = new LineChart<Number, Number>(x, y);

        int biggestXY = 0;
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).get(0).moreEq(new RationalNumber(biggestXY)))
                biggestXY = (int) Math.ceil(points.get(i).get(0).toFloat());
            if (points.get(i).get(1).moreEq(new RationalNumber(biggestXY)))
                biggestXY = (int) Math.ceil(points.get(i).get(1).toFloat());
        }
        biggestXY += 2;

        XYChart.Series solution = new XYChart.Series();
        ObservableList dataSolution = FXCollections.observableArrayList();
        for (ArrayList<RationalNumber> point : vertexes) {
            dataSolution.add(new XYChart.Data(point.get(0).toFloat(), point.get(1).toFloat()));
        }
        solution.setData(dataSolution);
        solution.setName("Vertexes of the shape");
        numberLineChart.getData().add(solution);


        float xDirection = function.get(1).toFloat() * -1;
        float yDirection = function.get(2).toFloat() * -1;

        if (xDirection > biggestXY) {
            float tmp = xDirection % biggestXY;
            xDirection = xDirection / tmp;
            yDirection = yDirection / tmp;
        }
        if (yDirection > biggestXY) {
            float tmp = yDirection % biggestXY;
            yDirection = yDirection / tmp;
            xDirection = xDirection / tmp;
        }


        ArrayList<ArrayList<RationalNumber>> lines = new ArrayList(table);
        lines.add(new ArrayList<>(List.of(function.get(simplex.freeVariables.get(0)), function.get(simplex.freeVariables.get(1)), new RationalNumber(0))));
        for (int i = 0; i < lines.size(); i++) {
            XYChart.Series series = new XYChart.Series();
            ObservableList data = FXCollections.observableArrayList();
            int j = -1;
            if (xDirection < 0)
                j = (int) xDirection;
            for (; j <= biggestXY; j++) {
                if (lines.get(i).get(1).equals(new RationalNumber(0))) {
                    data.add(new XYChart.Data(lines.get(i).get(2).divide(lines.get(i).get(0)).toFloat(), j));
                    continue;
                }
                RationalNumber yVal = lines.get(i).get(2).clone().divide(lines.get(i).get(1));
                RationalNumber qwe = lines.get(i).get(0).divide(lines.get(i).get(1)).multiply(new RationalNumber(j));
                yVal = yVal.subtraction(qwe);
                if (yVal.toFloat() < -5 || yVal.toFloat() > biggestXY)
                    continue;
                data.add(new XYChart.Data(j, yVal.toFloat()));
            }
            for (j = data.size() - 2; j > 0; j--)
                data.remove(j);
            series.setData(data);
            String name = lines.get(i).get(0) + "x" + simplex.freeVariables.get(0) + " ";
            if (lines.get(i).get(1).less(new RationalNumber(0)))
                name += lines.get(i).get(1) + "x" + simplex.freeVariables.get(1) + " = " + lines.get(i).get(2);
            else
                name += "+ " + lines.get(i).get(1) + "x" + simplex.freeVariables.get(1) + " = " + lines.get(i).get(2);
            series.setName(name);
            numberLineChart.getData().add(series);
        }
        numberLineChart.setPrefWidth(665);
        numberLineChart.setPrefHeight(665);


        XYChart.Series direction = new XYChart.Series();
        ObservableList dataDirection = FXCollections.observableArrayList();
        dataDirection.addAll(new XYChart.Data(0, 0),
                new XYChart.Data(xDirection, yDirection));
        direction.setData(dataDirection);
        direction.setName("-n");
        numberLineChart.getData().add(direction);


        return numberLineChart;
    }

    public String newFunctionToString() {
        StringBuilder str = new StringBuilder("f(x) = ");
        for (int i = 0; i < function.size(); i++) {
            if (function.get(i).equals(new RationalNumber(0)))
                continue;
            if (i == 0)
                str.append(function.get(i));
            else {
                if (function.get(i).less(new RationalNumber(0)))
                    str.append(" ").append(function.get(i)).append("x").append(i);
                else
                    str.append(" +").append(function.get(i)).append("x").append(i);
            }
        }
        if (str.toString().equals("f(x) = "))
            str.append("0");
        return str.toString();
    }
}
