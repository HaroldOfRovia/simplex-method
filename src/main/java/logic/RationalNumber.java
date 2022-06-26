package logic;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class RationalNumber {
    public static boolean decimal = false;
    private Integer numerator;
    private Integer denominator;

    public RationalNumber() {
        this.numerator = 1;
        this.denominator = 1;
    }

    public RationalNumber(int a) {
        this.numerator = a;
        this.denominator = 1;
    }

    public RationalNumber(int a, int b) throws Exception {
        if (b == 0)
            throw new Exception("Denominator is zero!");
        int reduce = gcd(a, b);
        a = a / reduce;
        b = b / reduce;
        if (b < 0) {
            b = b * (-1);
            a = a * (-1);
        }
        this.numerator = a;
        this.denominator = b;
    }

    public RationalNumber(String str) throws Exception {
        String[] numbers;
        if (str.contains(".")) {
            numbers = str.split("\\.");
        } else if (str.contains(",")) {
            numbers = str.split(",");
        } else if (str.contains("/")) {
            numbers = str.split("/");
            for (int i = 0; i < numbers.length; i++)
                numbers[i] = numbers[i].trim();
            numerator = Integer.parseInt(numbers[0]);
            denominator = Integer.parseInt(numbers[1]);
            if (denominator == 0)
                throw new Exception("Denominator is zero!");
            return;
        } else {
            numerator = Integer.parseInt(str);
            denominator = 1;
            return;
        }
        for (int i = 0; i < numbers.length; i++)
            numbers[i] = numbers[i].trim();
        RationalNumber a = new RationalNumber(Integer.parseInt(numbers[1]), 10);
        RationalNumber b = new RationalNumber(Integer.parseInt(numbers[0]));
        numerator = a.sum(b).numerator;
        denominator = a.sum(b).denominator;
    }

    public void setDecimal(boolean a) {
        decimal = a;
    }

    public void setNumber(RationalNumber a) {
        this.numerator = a.numerator;
        this.denominator = a.denominator;
    }

    @Override
    public RationalNumber clone() {
        try {
            return new RationalNumber(this.numerator, this.denominator);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private RationalNumber cut(RationalNumber a) {
        int reduce = gcd(a.numerator, a.denominator);
        a.numerator = a.numerator / reduce;
        a.denominator = a.denominator / reduce;
        if (a.denominator < 0) {
            a.denominator = a.denominator * (-1);
            a.numerator = a.numerator * (-1);
        }
        return a;
    }

    public RationalNumber reverse() throws Exception {
        return new RationalNumber(this.denominator, this.numerator);
    }

    /**
     * @param a - первое число
     * @param b - второе число
     * @return наибольший общий делитель
     */
    private Integer gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    /**
     * @param a - первое число
     * @param b - второе число
     * @return наименьшее общее кратниое
     */
    private Integer lcm(int a, int b) {
        return (Math.abs(a) * Math.abs(b)) / gcd(a, b);
    }

    public RationalNumber sum(RationalNumber second) throws Exception {
        int newDenominator = lcm(this.denominator, second.denominator),
                newNumerator = (this.numerator * newDenominator) / this.denominator + (second.numerator * newDenominator) / second.denominator;
        return cut(new RationalNumber(newNumerator, newDenominator));
    }

    public RationalNumber multiply(RationalNumber second) throws Exception {
        int newDenominator = this.denominator * second.denominator,
                newNumerator = this.numerator * second.numerator;
        return cut(new RationalNumber(newNumerator, newDenominator));
    }

    public RationalNumber subtraction(RationalNumber second) throws Exception {
        RationalNumber clone = second.clone();
        clone = clone.multiply(new RationalNumber(-1));
        return this.clone().sum(clone);
    }

    public RationalNumber divide(RationalNumber second) throws Exception {
        RationalNumber clone = second.clone();
        clone.setNumber(clone.reverse());
        return this.clone().multiply(clone);
    }

    @Override
    public String toString() {
        String tmp;
        if (decimal)
            tmp = Float.toString((float) numerator / (float) denominator);
        else {
            if (denominator == 1)
                tmp = Integer.toString(numerator);
            else
                tmp = numerator + "/" + denominator;
        }
        return tmp;
    }

    /**
     * @return a <= b
     */
    public boolean lessEq(RationalNumber second) {
        int newDenominator = lcm(this.denominator, second.denominator),
                a = this.numerator * (newDenominator / this.denominator),
                b = second.numerator * (newDenominator / second.denominator);
        return a <= b;
    }

    /**
     * @return a < b
     */
    public boolean less(RationalNumber second) {
        int newDenominator = lcm(this.denominator, second.denominator),
                a = this.numerator * (newDenominator / this.denominator),
                b = second.numerator * (newDenominator / second.denominator);
        return a < b;
    }

    /**
     * @return a >= b
     */
    public boolean moreEq(RationalNumber second) {
        int newDenominator = lcm(this.denominator, second.denominator),
                a = this.numerator * (newDenominator / this.denominator),
                b = second.numerator * (newDenominator / second.denominator);
        return a >= b;
    }

    /**
     * @return a > b
     */
    public boolean more(RationalNumber second) {
        int newDenominator = lcm(this.denominator, second.denominator),
                a = this.numerator * (newDenominator / this.denominator),
                b = second.numerator * (newDenominator / second.denominator);
        return a > b;
    }

    /**
     * @return a == b
     */
    public boolean equals(RationalNumber second) {
        int newDenominator = lcm(this.denominator, second.denominator),
                a = this.numerator * (newDenominator / this.denominator),
                b = second.numerator * (newDenominator / second.denominator);
        return a == b;
    }

    public boolean isZero() {
        return this.numerator == 0;
    }

    public Float toFloat() {
        return ((float) numerator) / ((float) denominator);
    }
}
