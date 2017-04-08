package com.mossle.internal.sequence.support;

import java.text.SimpleDateFormat;

import java.util.Date;

public class SequencePart {
    private String text;

    public SequencePart(String text) {
        this.text = text;
    }

    public String process(String code, Date date, SequenceHelper sequenceHelper) {
        if ("{YYYYMMDD}".equals(text)) {
            return new SimpleDateFormat("yyyyMMdd").format(date);
        } else if ("{N}".equals(text)) {
            return sequenceHelper.process(code, date);
        } else {
            return text;
        }
    }

    public String getText() {
        return text;
    }
}
