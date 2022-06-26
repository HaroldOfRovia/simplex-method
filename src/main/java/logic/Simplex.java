package logic;

import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simplex {
    public ArrayList<RationalNumber> function;
    public ArrayList<Integer> basis;
    public ArrayList<Integer> freeVariables;
    public ArrayList<ArrayList<RationalNumber>> table;
    public boolean isArtificial;

    public Simplex() {
        this.function = new ArrayList<RationalNumber>();
        this.table = new ArrayList<ArrayList<RationalNumber>>();
        this.basis = new ArrayList<Integer>();
        this.freeVariables = new ArrayList<Integer>();
    }

    public Simplex(List<RationalNumber> func, List<Integer> basis, List<List<RationalNumber>> matrix) throws Exception {
        this.function = new ArrayList<RationalNumber>(func);
        this.table = new ArrayList<ArrayList<RationalNumber>>();
        for (int i = 0; i < matrix.size(); i++) {
            this.table.add(i, new ArrayList<>(matrix.get(i)));
        }
        this.basis = new ArrayList<>(basis);
        this.freeVariables = new ArrayList<>();
        for (int i = 0; i < table.get(0).size() - 1; i++) {
            if (!this.basis.contains(i + 1))
                this.freeVariables.add((i + 1));
        }
        isArtificial = false;
    }

    public Simplex(List<RationalNumber> func, List<List<RationalNumber>> matrix) throws Exception {
        this.function = new ArrayList<RationalNumber>(func);
        this.table = new ArrayList<ArrayList<RationalNumber>>();
        for (int i = 0; i < matrix.size(); i++) {
            if (matrix.get(i).get(matrix.get(i).size() - 1).less(new RationalNumber(0))) {
                ArrayList<RationalNumber> tmp = new ArrayList<>();
                for (int j = 0; j < matrix.get(i).size(); j++) {
                    tmp.add(matrix.get(i).get(j).multiply(new RationalNumber(-1)));
                }
                this.table.add(i, tmp);
            } else
                this.table.add(i, new ArrayList<>(matrix.get(i)));
        }
        this.basis = new ArrayList<Integer>();
        for (int i = 0; i < table.size(); i++) {//заполение базиса
            basis.add(table.get(0).size() + i);
        }
        this.freeVariables = new ArrayList<Integer>();
        for (int i = 0; i < table.get(0).size() - 1; i++) {//заполение свободных переменных
            freeVariables.add(i + 1);
        }
        isArtificial = true;
    }

    @Override
    public Simplex clone() {
        Simplex cloneTable = new Simplex();
        cloneTable.function = (ArrayList<RationalNumber>) this.function.clone();
        cloneTable.basis = (ArrayList<Integer>) this.basis.clone();
        cloneTable.freeVariables = (ArrayList<Integer>) this.freeVariables.clone();
        for (int i = 0; i < this.table.size(); i++) {
            cloneTable.table.add((ArrayList<RationalNumber>) this.table.get(i).clone());
        }
        cloneTable.isArtificial = this.isArtificial;
        return cloneTable;
    }

    public Simplex toArtificial() throws Exception {
        Simplex task = new Simplex();
        task.function = function;
        task.table = table;
        task.basis = new ArrayList<>();
        task.makeFreeVariables();
        task.isArtificial = true;
        return task;
    }

    public void makeFreeVariables() {
        if (basis.size() == 0) {
            this.basis = new ArrayList<Integer>();
            for (int i = 0; i < table.size(); i++) {//заполение базиса
                basis.add(table.get(0).size() + i);
            }
            this.freeVariables = new ArrayList<Integer>();
            for (int i = 0; i < table.get(0).size() - 1; i++) {//заполение свободных переменных
                freeVariables.add(i + 1);
            }
        } else {
            this.freeVariables = new ArrayList<>();
            for (int i = 0; i < table.get(0).size() - 1; i++) {
                if (!this.basis.contains(i + 1))
                    this.freeVariables.add((i + 1));
            }
        }
    }

    public void gauss() throws Exception {
        int curStr = 0;
        ArrayList<Integer> finStr = new ArrayList<>();
        for (int i = 0; i < this.basis.size(); i++) {//метод гаусса
            int curBasVar = basis.get(i) - 1;

            if (table.get(curStr).get(curBasVar).isZero()) {//если текущий элемент 0, то ищем не 0
                for (int j = 0; j < table.size(); j++) {
                    if (finStr.contains(j))
                        continue;
                    if (!table.get(j).get(curBasVar).isZero()) {
                        ArrayList<RationalNumber> tmp = table.get(curStr);
                        table.set(curStr, (ArrayList<RationalNumber>) table.get(j).clone());
                        table.set(j, (ArrayList<RationalNumber>) tmp.clone());
                        break;
                    }
                }
                if (table.get(curStr).get(curBasVar).isZero()) {
                    throw new Exception("The Gauss method cannot be solved!");
                }
            }

            RationalNumber ratio = table.get(curStr).get(curBasVar).reverse();
            for (int j = 0; j < table.get(i).size(); j++) {//текущий элемент делаем единицей
                table.get(curStr).set(j, table.get(curStr).get(j).multiply(ratio));
            }
            for (int j = 0; j < table.size(); j++) {//идем по строке, чтоб вычесть из нее
                if (j == curStr)
                    continue;
                ratio = table.get(j).get(curBasVar).clone();
                for (int a = 0; a < table.get(j).size(); a++) {
                    table.get(j).set(a, table.get(j).get(a).subtraction(ratio.multiply(table.get(curStr).get(a))));
                }
            }
            finStr.add(curStr++);
        }

        for (int i = 0; i < table.size(); i++) {//проверка Гаусса и удаление пустых строк
            boolean isEmpty = true;
            for (int j = 0; j < table.get(i).size(); j++) {
                if (!table.get(i).get(j).isZero()) {
                    if (j == table.get(i).size() - 1)
                        throw new Exception("The current system cannot be resolved!");
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty) {//если вся строка 0, то удаляем ее
                table.remove(i);
            }
        }

        for (int i = 0; i < basis.size(); i++) {//пересчет целевой функции
            int curBasVar = basis.get(i) - 1;

            ArrayList<RationalNumber> tmp = new ArrayList<>();
            tmp.add(table.get(i).get(table.get(i).size() - 1));
            for (int j = 0; j < table.get(i).size() - 1; j++) {
                if (basis.contains(j + 1))
                    tmp.add(new RationalNumber(0));
                else {
                    tmp.add(table.get(i).get(j).multiply(new RationalNumber(-1)));
                }
            }
            RationalNumber ratio = function.get(curBasVar + 1);
            for (int j = 0; j < tmp.size(); j++) {
                function.set(j, function.get(j).sum(tmp.get(j).multiply(ratio)));
            }
            function.set(curBasVar + 1, new RationalNumber(0));
        }

        ArrayList<RationalNumber> tmp = new ArrayList<>();
        for (int i = 1; i < function.size(); i++) {//добавление последней строки
            tmp.add(function.get(i));
        }
        tmp.add(function.get(0).multiply(new RationalNumber(-1)));
        table.add(tmp);

        for (int i = table.get(0).size() - 2; i >= 0; i--) {//удаление базисных столобцов с 1
            if (basis.contains(i + 1)) {
                for (int j = 0; j < table.size(); j++)
                    table.get(j).remove(i);
            }
        }
    }

    public void buildTable() throws Exception {
        if (basis.get(0) < function.size()) {//если метод искусственного базиса, то гаусс не нужен
            gauss();
        } else {//таблица для метода искусственного базаиса
            ArrayList<RationalNumber> p = new ArrayList<>(Collections.nCopies(table.get(0).size(), new RationalNumber(0)));
            for (int i = 0; i < table.get(0).size(); i++) {
                for (int j = 0; j < table.size(); j++) {
                    p.set(i, p.get(i).subtraction(table.get(j).get(i)));
                }
            }
            table.add(p);
        }
    }

    public boolean isSolved() {
        boolean isSolved = false;
        for (int i = 0; i < table.get(0).size() - 1; i++) {
            if (table.get(table.size() - 1).get(i).less(new RationalNumber(0)))
                break;
            if (i == table.get(0).size() - 2)
                isSolved = true;
        }
        return isSolved;
    }

    public Label getAnswer() throws Exception {
        if (isArtificial) {
            if (freeVariables.size() + basis.size() > function.size() - 1)
                return new Label("Answer: Limitations of the original task are incompatible");
            else if (table.get(table.size() - 1).get(table.get(0).size() - 1).equals(new RationalNumber(0)))
                return new Label();
        }
        Label label = new Label();
        StringBuilder str = new StringBuilder("Answer: x(");
        for (int i = 1; i < function.size(); i++) {
            if (i == function.size() - 1) {
                if (basis.contains(i))
                    str.append(table.get(basis.indexOf(i)).get(table.get(0).size() - 1)).append(")");
                else
                    str.append("0)");
            } else {
                if (basis.contains(i))
                    str.append(table.get(basis.indexOf(i)).get(table.get(0).size() - 1)).append(", ");
                else
                    str.append("0, ");
            }
        }

        str.append(", f=").append(table.get(table.size() - 1).get(table.get(0).size() - 1).multiply(new RationalNumber(-1)));
        label.setText(str.toString());

        return label;
    }

    /**
     * если на вход (-1,-1), то элемент выбирается программой
     * отсчет элементов с (0,0)
     *
     * @param xPos позиция элемента в строке
     * @param yPos позиция элемента в столбце
     * @return
     */
    public Simplex nextStep(int xPos, int yPos) throws Exception {
        if (isArtificial)
            for (int i = 0; i < table.size() - 1; i++) {
                if (table.get(i).get(table.get(0).size() - 1).less(new RationalNumber(0)))
                    throw new Exception("Basis contains negative number!");
            }
        else if (freeVariables.size() == 0) {
            throw new Exception("It`s solved!");
        }
        Simplex nextTable = clone();

        int whatDelete = -1;
        if (isArtificial) {
            for (int i = 0; i < freeVariables.size(); i++) {
                if (nextTable.freeVariables.get(i) > nextTable.function.size() - 1) {
                    whatDelete = i;
                    break;
                }
            }
            if (whatDelete != -1) {
                nextTable.freeVariables.remove(whatDelete);

                for (int i = 0; i < nextTable.table.size(); i++) {
                    nextTable.table.get(i).remove(whatDelete);
                }
            }
            if (xPos != -1 && whatDelete < xPos && whatDelete != -1)
                xPos--;
        }

        if (isSolved()) {
            if (isArtificial) {
                if (nextTable.freeVariables.size() + nextTable.basis.size() > nextTable.function.size() - 1)
                    throw new Exception("Limitations of the original task are incompatible");
                else if (nextTable.table.get(nextTable.table.size() - 1).get(nextTable.table.get(0).size() - 1).equals(new RationalNumber(0)))
                    return nextTable.rebuildTable();
            } else
                throw new Exception("It`s solved!");
        }

        if (xPos == -1) { //выбор элемента для переделывания таблицы
            ArrayList<Integer> allowed = nextTable.allowedPositions();
            for (int i = 0; i < nextTable.table.get(0).size() - 1; i++) {
                if (yPos != -1)
                    break;
                if (nextTable.table.get(table.size() - 1).get(i).less(new RationalNumber(0))) {
                    xPos = i;
                    if (isArtificial) {
                        if (allowed.get(i) == -1) {
                            continue;
                        }
                        for (int j = 0; j < nextTable.table.size() - 1; j++) {
                            if (nextTable.basis.get(j) > function.size() - 1 && !nextTable.table.get(j).get(i).equals(new RationalNumber(0))) {
                                RationalNumber a = nextTable.table.get(j).get(nextTable.table.get(j).size() - 1).divide(nextTable.table.get(j).get(i));
                                RationalNumber b = nextTable.table.get(allowed.get(i)).get(nextTable.table.get(j).size() - 1).divide(nextTable.table.get(allowed.get(i)).get(i));
                                if (a.equals(b)) {
                                    yPos = j;
                                    break;
                                }
                            }
                        }
                    } else
                        break;
                }
            }

            if (yPos == -1)
                yPos = nextTable.allowedPositions().get(xPos);
            if (yPos == -1)
                throw new Exception("The function is not limited, there are no solutions!");

        } else {
            for (int i = 0; i < table.size() - 1; i++) {//проверка, что это минимальное отношение
                if (nextTable.table.get(i).get(xPos).lessEq(new RationalNumber(0)))
                    continue;
                RationalNumber a = nextTable.table.get(i).get(nextTable.table.get(0).size() - 1).divide(nextTable.table.get(i).get(xPos));
                RationalNumber b = nextTable.table.get(yPos).get(nextTable.table.get(0).size() - 1).divide(nextTable.table.get(yPos).get(xPos));
                if (nextTable.table.get(i).get(nextTable.table.get(0).size() - 1).divide(nextTable.table.get(i).get(xPos))//новое отношение
                        .less(nextTable.table.get(yPos).get(nextTable.table.get(0).size() - 1).divide(nextTable.table.get(yPos).get(xPos)))) {//меньше чем старое
                    throw new Exception("The wrong element is selected!");
                }
            }
        }

        if (nextTable.table.get(yPos).get(xPos).lessEq(new RationalNumber(0)))
            throw new Exception("The selected item is less than 0!");


        //начало перестроения
        int tmpXNum = nextTable.basis.get(yPos);
        nextTable.basis.set(yPos, nextTable.freeVariables.get(xPos));
        nextTable.freeVariables.set(xPos, tmpXNum);

        nextTable.table.get(yPos).set(xPos, nextTable.table.get(yPos).get(xPos).reverse());
        for (int i = 0; i < nextTable.table.get(0).size(); i++) {
            if (i == xPos)
                continue;
            nextTable.table.get(yPos).set(i, nextTable.table.get(yPos).get(xPos).multiply(nextTable.table.get(yPos).get(i)));
        }
        for (int i = 0; i < nextTable.table.size(); i++) {
            if (i == yPos)
                continue;
            nextTable.table.get(i).set(xPos, nextTable.table.get(yPos).get(xPos).multiply(nextTable.table.get(i).get(xPos)).multiply(new RationalNumber(-1)));
        }

        for (int i = 0; i < nextTable.table.size(); i++) {//изменение остальных элементов
            if (i == yPos)
                continue;

            ArrayList<RationalNumber> tmp = new ArrayList<>();
            ArrayList<RationalNumber> yStrCoeff = (ArrayList<RationalNumber>) nextTable.table.get(yPos).clone();
            for (int a = 0; a < yStrCoeff.size(); a++) {//массив, который буду вычитать (т.к. -1, то уже складывать)
                int x = xPos;
                if (table.get(0).size() != nextTable.table.get(0).size() && xPos >= whatDelete && whatDelete != -1)
                    x++;
                yStrCoeff.set(a, yStrCoeff.get(a).multiply(new RationalNumber(-1)).multiply(table.get(i).get(x)));
            }

            for (int j = 0; j < nextTable.table.get(i).size(); j++) {
                if (j == xPos)
                    tmp.add(nextTable.table.get(i).get(xPos));
                else
                    tmp.add(nextTable.table.get(i).get(j).sum(yStrCoeff.get(j)));
            }
            nextTable.table.set(i, tmp);
        }

        return nextTable;
    }

    /**
     * перестраивает таблицу под нормальное решение
     * перестраивает функцию
     */
    private Simplex rebuildTable() throws Exception {
        Simplex nextTable = this.clone();
        nextTable.isArtificial = false;
        for (int i = 0; i < nextTable.table.size() - 1; i++) {
            RationalNumber ratio = nextTable.function.get(nextTable.basis.get(i));
            nextTable.function.set(nextTable.basis.get(i), new RationalNumber(0));
            for (int j = 0; j < nextTable.table.get(0).size(); j++) {
                try {
                    int curPos = nextTable.freeVariables.get(j);
                    nextTable.function.set(curPos, nextTable.table.get(i).get(j).multiply(new RationalNumber(-1)).multiply(ratio)
                            .sum(nextTable.function.get(curPos)));
                } catch (Exception ex) {
                    nextTable.function.set(0, nextTable.table.get(i).get(j).multiply(ratio).sum(nextTable.function.get(0)));
                }
            }
        }

        for (int i = 0; i < nextTable.freeVariables.size(); i++) {
            nextTable.table.get(nextTable.table.size() - 1).set(i, nextTable.function.get(nextTable.freeVariables.get(i)));
        }
        nextTable.table.get(nextTable.table.size() - 1).set(nextTable.table.get(0).size() - 1, nextTable.function.get(0).multiply(new RationalNumber(-1)));
        return nextTable;
    }

    public ArrayList<Integer> allowedPositions() throws Exception {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < table.get(0).size() - 1; i++) {
            int yPos = -1;
            for (int j = 0; j < table.size() - 1; j++) {
                if (table.get(j).get(i).more(new RationalNumber(0))) {
                    yPos = j;
                    break;
                }
            }

            for (int j = 0; j < table.size() - 1; j++) {
                if (table.get(j).get(i).lessEq(new RationalNumber(0)))
                    continue;
                if (table.get(j).get(table.get(0).size() - 1).divide(table.get(j).get(i))//новое отношение
                        .less(table.get(yPos).get(table.get(0).size() - 1).divide(table.get(yPos).get(i)))) {//меньше чем старое
                    yPos = j;
                }
            }
            list.add(yPos);
        }
        return list;
    }

    public ArrayList<ArrayList<Boolean>> buttonPosition() throws Exception {
        ArrayList<ArrayList<Boolean>> buttonPosition = new ArrayList<>();
        ArrayList<Integer> allowed = allowedPositions();

        if (isSolved())
            for (int i = 0; i < table.size(); i++) {
                buttonPosition.add(new ArrayList<>());
                for (int j = 0; j < table.get(0).size(); j++) {
                    buttonPosition.get(i).add(false);
                }
            }
        else
            for (int i = 0; i < table.size(); i++) {
                buttonPosition.add(new ArrayList<>());
                for (int j = 0; j < table.get(0).size(); j++) {
                    if (i == table.size() - 1 || j == table.get(0).size() - 1 || allowed.get(j) == -1 || table.get(i).get(j).equals(new RationalNumber(0)) ||
                            freeVariables.get(j) > function.size() - 1) {
                        buttonPosition.get(i).add(false);
                        continue;
                    }
                    if (table.get(i).get(table.get(0).size() - 1).divide(table.get(i).get(j))
                            .equals(table.get(allowed.get(j)).get(table.get(0).size() - 1).divide(table.get(allowed.get(j)).get(j))))
                        buttonPosition.get(i).add(true);
                    else
                        buttonPosition.get(i).add(false);
                }
            }

        return buttonPosition;
    }

    public String functionToString() {
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
