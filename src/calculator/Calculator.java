package calculator;

import java.nio.file.spi.FileSystemProvider;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
class Calculator {

    String calculate(String[] expression) {
        List<String> tmp = new ArrayList<>(List.of(expression));
        int brOpenInto;
        do {
            brOpenInto = -1;
            for (int i = 0; i < tmp.size(); i++) {
                String a = tmp.get(i);
                if (a.equals("(")) {
                    brOpenInto = i;

                } else if (a.equals(")")) {
                    var inbr = tmp.subList(brOpenInto + 1, i);
                    var result = calculate(inbr);
                    tmp.subList(brOpenInto, i + 1).clear();
                    tmp.add(brOpenInto, result);
                    break;
                }

            }

        }    while (brOpenInto != -1);


        return calculate(tmp);
    }

    String calculate(List<String> expression) {

        List<String> tmp = new ArrayList<>();
        double a = Double.parseDouble(expression.get(0));
        for (int i = 1; i < expression.size(); i += 2) {
            String op = expression.get(i);
            double b = Double.parseDouble(expression.get(i + 1));
            switch (op) {
                case "+":
                case "-":
                    tmp.add(String.valueOf(a));
                    tmp.add(op);
                    a = b;
                    break;
                case "*":
                    a *= b;
                    break;
                case "/":
                    a /= b;
                    break;
            }

        }
        tmp.add(String.valueOf(a));
        System.out.println(tmp);

        double result = Double.parseDouble(tmp.get(0));
        for (int i = 1; i < tmp.size(); i += 2) {
            String op = tmp.get(i);
            double b = Double.parseDouble(tmp.get(i + 1));
            switch (op) {
                case "+":
                    result += b;
                    break;
                case "-":
                    result -= b;
                    break;
                default:
                    return "Error";
            }
        }


        return String.valueOf(result);

    }


}
