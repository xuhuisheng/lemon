package com.mossle.internal.sequence.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SequenceParser {
    public SequenceRule parse(String text) {
        List<SequencePart> sequenceParts = new ArrayList<SequencePart>();
        StringBuilder buff = new StringBuilder();
        boolean inBacket = false;

        for (int i = 0, len = text.length(); i < len; i++) {
            char c = text.charAt(i);

            switch (c) {
            case '{':

                if (inBacket) {
                    throw new IllegalStateException("invalid rule : " + text
                            + ", index : " + i + ", char : " + c);
                }

                if (buff.length() > 0) {
                    sequenceParts.add(new SequencePart(buff.toString()));
                    buff = new StringBuilder();
                }

                buff.append("{");
                inBacket = true;

                break;

            case '}':

                if (!inBacket) {
                    // throw new IllegalStateException("invalid rule : " + text + ", index : " + i + ", char : " + c);
                    buff.append(c);

                    break;
                }

                if (buff.length() == 0) {
                    throw new IllegalStateException("invalid rule : " + text
                            + ", index : " + i + ", char : " + c);
                }

                buff.append("}");
                sequenceParts.add(new SequencePart(buff.toString()));
                buff = new StringBuilder();
                inBacket = false;

                break;

            default:
                buff.append(c);
            }
        }

        if (buff.length() > 0) {
            sequenceParts.add(new SequencePart(buff.toString()));
        }

        SequenceRule sequenceRule = new SequenceRule(sequenceParts);

        return sequenceRule;
    }
}
