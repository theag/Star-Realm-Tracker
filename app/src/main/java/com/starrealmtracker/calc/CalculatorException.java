package com.starrealmtracker.calc;

/**
 * Created by nbp184 on 2017/05/04.
 */
public class CalculatorException extends Exception {

    private int code;
    /*
    0 - illegal character
    1 - mismatched bracket
    2 - division by zero
    */


    public CalculatorException(int code, String message) {
        super(message);
        this.code = code;
    }

}
