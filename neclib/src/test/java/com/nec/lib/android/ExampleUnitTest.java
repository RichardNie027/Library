package com.nec.lib.android;

import com.nec.lib.android.utils.Calculator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void stringCalculator() {
        String expression = "(0*1--3)-5/-4-(3*(-2.13))";
        expression = "-033*-3-2";
        double result = Calculator.conversion(expression);
        System.out.println(expression + " = " + result);
    }
}