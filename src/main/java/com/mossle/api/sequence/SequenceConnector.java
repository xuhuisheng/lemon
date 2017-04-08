package com.mossle.api.sequence;

import java.util.Date;

public interface SequenceConnector {
    String generate(String code, String text);

    String generate(String code, String text, Date date);
}
