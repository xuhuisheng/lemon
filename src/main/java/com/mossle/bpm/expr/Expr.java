package com.mossle.bpm.expr;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Expr {
    private List<String> opers = new ArrayList<String>();

    public Expr() {
        // 优先级，&&高于||
        opers.add("&&");
        opers.add("||");
        opers.add("(");
        opers.add(")");
    }

    public List<String> evaluate(String text, ExprProcessor exprProcessor) {
        List<Token> tokens = parse(text);
        Stack<List<String>> stack = new Stack<List<String>>();

        for (Token token : tokens) {
            if (token.isOper()) {
                List<String> right = stack.pop();
                List<String> left = stack.pop();
                List<String> value = exprProcessor.process(left, right,
                        token.getValue());
                stack.push(value);
            } else {
                stack.push(exprProcessor.process(token.getValue()));
            }
        }

        return stack.pop();
    }

    public List<Token> parse(String text) {
        List<Token> tokens = lex(text);

        Stack<Token> tokenStack = new Stack<Token>();
        List<Token> output = new ArrayList<Token>();

        for (Token token : tokens) {
            processToken(token, tokenStack, output);
        }

        popTokenStack(tokenStack, output, false);

        return output;
    }

    /**
     * 依次对token进行处理，这个过程是把token按照反序排列.
     * <p>
     * 原表达式为：“OA组 || 平台组”，处理后就变成：“OA组 平台组 ||”
     * </p>
     */
    public void processToken(Token token, Stack<Token> tokenStack,
            List<Token> output) {
        if (token.isOper()) {
            processOper(token, tokenStack, output);
        } else {
            processSymb(token, output);
        }
    }

    /**
     * 处理操作符.
     */
    public void processOper(Token token, Stack<Token> tokenStack,
            List<Token> output) {
        if ("(".equals(token.getValue())) {
            tokenStack.push(token);

            return;
        }

        if (")".equals(token.getValue())) {
            popTokenStack(tokenStack, output, true);

            return;
        }

        if (tokenStack.empty()) {
            tokenStack.push(token);

            return;
        }

        Token innerToken = tokenStack.peek();

        // 越靠前，索引越小，优先级越高
        if (opers.indexOf(innerToken.getValue()) <= opers.indexOf(token
                .getValue())) {
            // 如果当前token的优先级低于栈顶的操作符优先级，就弹出栈顶的操作符
            output.add(tokenStack.pop());
        }

        tokenStack.push(token);
    }

    /**
     * 处理操作值.
     */
    public void processSymb(Token token, List<Token> output) {
        output.add(token);
    }

    /**
     * 把堆栈中保存的操作符都弹出来，忽略().
     */
    public void popTokenStack(Stack<Token> tokenStack, List<Token> output,
            boolean breakWhenLeftBracket) {
        while (!tokenStack.empty()) {
            Token token = tokenStack.pop();

            if ("(".equals(token.getValue())) {
                if (breakWhenLeftBracket) {
                    return;
                } else {
                    continue;
                }
            }

            output.add(token);
        }
    }

    public List<Token> lex(String text) {
        List<Token> tokens = new ArrayList<Token>();
        StringBuilder buff = new StringBuilder();
        Oper bracket = null;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            switch (c) {
            case ' ':

                if (buff.length() > 0) {
                    String value = buff.toString();
                    buff = new StringBuilder();

                    if (opers.contains(value)) {
                        Oper oper = new Oper();
                        oper.setValue(value);
                        tokens.add(oper);
                    } else {
                        Symb symb = new Symb();
                        symb.setValue(value);
                        tokens.add(symb);
                    }
                }

                break;

            case '(':

                if (buff.length() > 0) {
                    String value = buff.toString();
                    buff = new StringBuilder();

                    if (opers.contains(value)) {
                        Oper oper = new Oper();
                        oper.setValue(value);
                        tokens.add(oper);
                    } else {
                        Symb symb = new Symb();
                        symb.setValue(value);
                        tokens.add(symb);
                    }
                }

                bracket = new Oper();
                bracket.setValue("(");
                tokens.add(bracket);

                break;

            case ')':

                if (buff.length() > 0) {
                    String value = buff.toString();
                    buff = new StringBuilder();

                    if (opers.contains(value)) {
                        Oper oper = new Oper();
                        oper.setValue(value);
                        tokens.add(oper);
                    } else {
                        Symb symb = new Symb();
                        symb.setValue(value);
                        tokens.add(symb);
                    }
                }

                bracket = new Oper();
                bracket.setValue(")");
                tokens.add(bracket);

                break;

            default:
                buff.append(c);

                break;
            }
        }

        if (buff.length() > 0) {
            String value = buff.toString();

            if (opers.contains(value)) {
                Oper oper = new Oper();
                oper.setValue(value);
                tokens.add(oper);
            } else {
                Symb symb = new Symb();
                symb.setValue(value);
                tokens.add(symb);
            }
        }

        return tokens;
    }
}
