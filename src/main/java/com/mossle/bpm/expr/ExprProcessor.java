package com.mossle.bpm.expr;

import java.util.List;

public interface ExprProcessor {
    List<String> process(List<String> left, List<String> right, String operation);

    List<String> process(String text);
}
