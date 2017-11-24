package com.mossle.internal.sequence.support;

import java.util.Date;

public class MockSequenceHelper implements SequenceHelper {
    public String process(String code, Date date) {
        return "1";
    }
}
