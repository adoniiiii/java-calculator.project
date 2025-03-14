import java.util.*;

public class Calculator {
    private static final List<String> history = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Calculator!");

        while (true) {
            System.out.print("Enter an expression (or type 'history' or 'exit'): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("history")) {
                printHistory();
                continue;
            } else if (input.equalsIgnoreCase("exit")) {
                System.out.println("Thank you for using the Calculator!");
                break;
            }

            try {
                double result = evaluateExpression(input);
                System.out.println("Result: " + result);
                history.add(input + " = " + result);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.print("Do you want to continue? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("n")) {
                System.out.println("Thank you for using the Calculator!");
                break;
            }
        }
        scanner.close();
    }

    private static double evaluateExpression(String expression) {
        try {
            final String expr = expression.replaceAll("\\s+", "");
            return new Object() {
                int pos = -1, ch;

                void nextChar() {
                    ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    while (true) {
                        if (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    while (true) {
                        if (eat('*')) x *= parseFactor();
                        else if (eat('/')) {
                            double divisor = parseFactor();
                            if (divisor == 0) throw new ArithmeticException("Division by zero");
                            x /= divisor;
                        } else if (eat('%')) x %= parseFactor();
                        else return x;
                    }
                }

                double parseFactor() {
                    if (eat('+')) return parseFactor();
                    if (eat('-')) return -parseFactor();

                    double x;
                    int startPos = this.pos;

                    if (Character.isLetter(ch)) {
                        StringBuilder func = new StringBuilder();
                        while (Character.isLetter(ch)) {
                            func.append((char) ch);
                            nextChar();
                        }

                        String functionName = func.toString();
                        eat('(');
                        double argument = parseExpression();
                        eat(')');

                        switch (functionName) {
                            case "sqrt": x = Math.sqrt(argument); break;
                            case "abs": x = Math.abs(argument); break;
                            case "round": x = Math.round(argument); break;
                            case "power":
                                eat(',');
                                double exponent = parseExpression();
                                eat(')');
                                x = Math.pow(argument, exponent);
                                break;
                            default: throw new RuntimeException("Unknown function: " + functionName);
                        }
                    } else if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(expr.substring(startPos, this.pos));
                    }
                    return x;
                }
            }.parse();
        } catch (Exception e) {
            throw new RuntimeException("Invalid expression format");
        }
    }

    private static void printHistory() {
        if (history.isEmpty()) {
            System.out.println("No history available.");
        } else {
            System.out.println("Calculation History:");
            history.forEach(System.out::println);
        }
    }
}
