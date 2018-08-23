package calculator;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.util.stream.Collectors;

public class CalculatorController {
    private static final Calculator calculator = new Calculator();
    private final BooleanProperty error = new SimpleBooleanProperty();
    private final StringProperty currentDigits = new SimpleStringProperty("");
    private final ObservableList<Token> tokens = FXCollections.observableArrayList();
    private final StringProperty memory = new SimpleStringProperty();
    public Label expressionText;
    public Label numberText;
    public Text memoryIndicator;
    private boolean showingResult;

    public void initialize() {
        expressionText.textProperty().bind(Bindings.createStringBinding(() -> {
            if (tokens.isEmpty()) {
                return "";
            }
            StringBuffer result = new StringBuffer(50);
            tokens.forEach(t -> result.append(t.getValue()));
            return result.toString();
        }, tokens));
        numberText.textProperty().bind(
                Bindings.when(error)
                        .then("Error")
                        .otherwise(currentDigits)
        );
        memoryIndicator.visibleProperty().bind(memory.isNotNull());
    }

    private void digit(String digit) {
        resetResult();
        Token last = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
        if (last == null || last.isOpenBracket() || last.isOperation()) {
            String text = currentDigits.get();
            currentDigits.set(text + digit);
        }
    }

    private void resetResult() {
        if (error.get()) {
            error.set(false);
            currentDigits.set("");
        } else if (showingResult) {
            clear();
        }
        showingResult = false;
    }

    private String format(String digits) {
        if (digits == null || digits.isEmpty()) {
            return "";
        }
        if (digits.contains(".")) {
            while (digits.endsWith("0")) {
                digits = digits.substring(0, digits.length() - 1);
            }
        }
        if (digits.endsWith(".")) {
            digits = digits.substring(0, digits.length() - 1);
        }
        boolean negative = digits.startsWith("-");
        if (negative) {
            digits = digits.substring(1);
        }
        while (digits.startsWith("0")) {
            digits = digits.substring(1);
        }
        if (digits.isEmpty()) {
            return "0";
        }
        if (digits.startsWith(".")) {
            digits = "0" + digits;
        }
        if (negative) {
            digits = "-" + digits;
        }
        return digits;
    }

    private void operation(Token operation) {
        String digits = format(currentDigits.get());
        if (digits.isEmpty() && tokens.isEmpty()) {
            return;
        }
        resetResult();
        Token last = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
        if ((last == null || last.isOperation()) && digits.isEmpty()) {
            tokens.set(tokens.size() - 1, operation);
        } else if (last == null || last.isOperation() || last.isOpenBracket()) {
            tokens.addAll(Token.number(digits), operation);
            currentDigits.set("");
        } else if (last.isCloseBracket()) {
            tokens.add(operation);
        }
    }

    private void clear() {
        currentDigits.set("");
        tokens.clear();
        error.set(false);
    }

    public void keyPressZero() {
        digit("0");
    }

    public void keyPressOne() {
        digit("1");
    }

    public void keyPressTwo() {
        digit("2");
    }

    public void keyPressThree() {
        digit("3");
    }

    public void keyPressFour() {
        digit("4");
    }

    public void keyPressFive() {
        digit("5");
    }

    public void keyPressSix() {
        digit("6");
    }

    public void keyPressSeven() {
        digit("7");
    }

    public void keyPressEight() {
        digit("8");
    }

    public void keyPressNine() {
        digit("9");
    }

    public void keyPressPlus() {
        operation(Token.operationAdd());
    }

    public void keyPressMinus() {
        operation(Token.operationSub());
    }

    public void keyPressDivide() {
        operation(Token.operationDiv());
    }

    public void keyPressMultiply() {
        operation(Token.operationMul());
    }

    public void keyPressResult() {
        resetResult();
        if (tokens.isEmpty()) {
            return;
        }
        if (calcOpenBracketCount() > 0) {
            error.set(true);
            return;
        }
        String digits = format(currentDigits.get());
        Token last = tokens.get(tokens.size() - 1);
        if (last.isOperation() && digits.isEmpty()) {
            tokens.remove(tokens.size() - 1);
        } else if (last.isOperation() && !digits.isEmpty()) {
            tokens.add(Token.number(digits));
            currentDigits.set("");
        }

        String[] expr = tokens.stream().map(Token::getAlternativeValue).collect(Collectors.toList()).toArray(new String[tokens.size()]);
        System.out.print("expression: ");
        System.out.println(String.join(" ", expr));
        showingResult = true;
        try {
            String result = calculator.calculate(expr);
            currentDigits.set(format(result));
            System.out.println("result: " + result);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            currentDigits.set("");
            error.set(true);
        }
    }

    public void keyPressDot() {
        resetResult();
        String text = currentDigits.get();
        if (text.contains(".")) {
            return;
        }
        if (text.isEmpty()) {
            text = "0";
        } else if (text.equals("-")) {
            text = "-0";
        }
        currentDigits.set(text + ".");
    }

    public void keyPressNeg() {
        resetResult();
        String text = currentDigits.get();
        if (text.startsWith("-")) {
            currentDigits.set(text.substring(1));
        } else {
            currentDigits.set("-" + text);
        }
    }

    public void keyPressOpenBracket() {
        resetResult();
        String digits = format(currentDigits.get());
        Token last = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
        if ((last == null || last.isOperation() || last.isOpenBracket()) && digits.isEmpty()) {
            tokens.add(Token.openBracket());
        }
    }

    public void keyPressClosedBracket() {
        resetResult();
        if (tokens.isEmpty()) {
            return;
        }
        int openCount = calcOpenBracketCount();
        if (openCount == 0) {
            return;
        }
        String digits = format(currentDigits.get());
        Token last = tokens.get(tokens.size() - 1);
        if (digits.isEmpty() && last.isCloseBracket()) {
            tokens.add(Token.closeBracket());
        } else if (!digits.isEmpty() && (last.isOperation() || last.isOpenBracket())) {
            tokens.add(Token.number(digits));
            tokens.add(Token.closeBracket());
            currentDigits.set("");
        }
    }

    private int calcOpenBracketCount() {
        int openCount = 0;
        for (Token t : tokens) {
            if (t.isOpenBracket()) {
                openCount++;
            } else if (t.isCloseBracket()) {
                openCount--;
            }
        }
        return openCount;
    }

    public void keyPressMemRead() {
        String mem = memory.get();
        if (mem == null || mem.isEmpty()) {
            return;
        }
        Token last = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
        if (last == null || last.isOpenBracket() || last.isOperation()) {
            currentDigits.set(mem);
        }
    }

    public void keyPressMemAdd() {
        String digits = format(currentDigits.get());
        if (digits.isEmpty()) {
            return;
        }
        String mem = memory.get();
        if (mem == null || mem.isEmpty()) {
            mem = "0";
        }
        mem = calculator.calculate(new String[]{mem, "+", digits});
        memory.set(format(mem));
    }

    public void keyPressMemClear() {
        memory.set(null);
    }

    public void keyPressBackspace() {
        resetResult();
        String text = currentDigits.get();
        if (text.isEmpty()) {
            if (!tokens.isEmpty()) {
                Token last = tokens.get(tokens.size() - 1);
                if (last.isOpenBracket()) {
                    tokens.remove(tokens.size() - 1);
                } else if (last.isOperation() || last.isCloseBracket()) {
                    Token prevToken = tokens.get(tokens.size() - 2);
                    if (prevToken.isNumber()) {
                        currentDigits.set(prevToken.getValue());
                        tokens.remove(tokens.size() - 2, tokens.size());
                    } else {
                        tokens.remove(tokens.size() - 1);
                    }
                }
            }
        } else {
            currentDigits.set(text.substring(0, text.length() - 1));
        }
    }

    public void keyPressClear() {
        clear();
    }


    private static abstract class Token {

        static Token number(String value) {
            return new NumberToken(value);
        }

        static Token operationAdd() {
            return new AddToken();
        }

        static Token operationSub() {
            return new SubToken();
        }

        static Token operationDiv() {
            return new DivToken();
        }

        static Token operationMul() {
            return new MulToken();
        }

        static Token openBracket() {
            return new BracketToken(true);
        }

        static Token closeBracket() {
            return new BracketToken(false);
        }

        abstract String getValue();

        String getAlternativeValue() {
            return getValue();
        }

        boolean isNumber() {
            return false;
        }

        boolean isOperation() {
            return false;
        }

        boolean isOpenBracket() {
            return false;
        }

        boolean isCloseBracket() {
            return false;
        }
    }

    private static class NumberToken extends Token {
        String value;

        NumberToken(String value) {
            this.value = value;
        }

        @Override
        String getValue() {
            return value;
        }

        @Override
        boolean isNumber() {
            return true;
        }
    }

    private static abstract class OperationToken extends Token {
        @Override
        boolean isOperation() {
            return true;
        }
    }

    private static class AddToken extends OperationToken {
        @Override
        String getValue() {
            return "\u002B";
        }

        @Override
        String getAlternativeValue() {
            return "+";
        }
    }

    private static class SubToken extends OperationToken {
        @Override
        String getValue() {
            return "\u2212";
        }

        @Override
        String getAlternativeValue() {
            return "-";
        }
    }

    private static class MulToken extends OperationToken {
        @Override
        String getValue() {
            return "\u00D7";
        }

        @Override
        String getAlternativeValue() {
            return "*";
        }
    }

    private static class DivToken extends OperationToken {
        @Override
        String getValue() {
            return "\u00F7";
        }

        @Override
        String getAlternativeValue() {
            return "/";
        }
    }

    private static class BracketToken extends Token {
        final boolean isOpen;

        BracketToken(boolean isOpen) {
            this.isOpen = isOpen;
        }

        @Override
        String getValue() {
            return isOpen ? "(" : ")";
        }

        @Override
        boolean isOpenBracket() {
            return isOpen;
        }

        @Override
        boolean isCloseBracket() {
            return !isOpen;
        }
    }
}
