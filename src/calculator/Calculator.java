package calculator;

import java.nio.file.spi.FileSystemProvider;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
class Calculator {

    String calculate(String[] expression) {
        System.out.println(expression[0]);
        System.out.println(expression[1]);
        System.out.println(expression[2]);
        //System.out.println(expression[3]);
        //System.out.println(expression[4]);
        double a = Double.parseDouble(expression[0]);
        double b = Double.parseDouble(expression[2]);


        double result;

        switch (expression[1]) {
            case "+":
                result = a + b;
                break;
            case "-":
                result = a - b;
                break;
            case "*":
                result = a * b;
                break;
            case "/":
                result = a / b;
                break;
            default:
                return "Error";
        }

        if (expression.length < 4) {
            return String.valueOf(result);
        }

        double c = Double.parseDouble(expression[4]);

        if (expression[1] == "*" && expression[3] == "+") {
            result = a * b + c;
        } else if (expression[1] == "*" && expression[3] == "-") {
            result = a * b - c;
        } else if (expression[1] == "/" && expression[3] == "+") {
            result = a / b + c;
        } else if (expression[1] == "/" && expression[3] == "-") {
            result = a / b - c;
        } else if (expression[1] == "/" && expression[3] == "+") {
            result = a / b + c;
        } else if (expression[1] == "+" && expression[3] == "*") {
            result = c * b + a;
        } else if (expression[1] == "+" && expression[3] == "/") {
            result = c / b + a;
        } else if (expression[1] == "-" && expression[3] == "*") {
            result = a - (c * b);
        } else if (expression[1] == "-" && expression[3] == "/") {
            result = a - (b / c);
        }
        /*switch (expression[3]) {
            case "+":
                result = result + c;
                break;
            case "-":
                result = result - c;
                break;
            case "*":
                result = result * c;
                break;
            case "/":
                result = result / c;
                break;
            default:
                return "Error";
        }*/

        return String.valueOf(result);


    }

}
