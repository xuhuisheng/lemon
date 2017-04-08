package com.mossle.internal.sequence.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SequenceRule {
    private List<SequencePart> sequenceParts;

    public SequenceRule(List<SequencePart> sequenceParts) {
        this.sequenceParts = sequenceParts;
    }

    public String process(String code, Date date, SequenceHelper sequenceHelper) {
        StringBuilder buff = new StringBuilder();

        for (SequencePart sequencePart : sequenceParts) {
            buff.append(sequencePart.process(code, date, sequenceHelper));
        }

        return buff.toString();
    }

    public List<SequencePart> getSequenceParts() {
        return sequenceParts;
    }
}
