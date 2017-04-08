package com.mossle.internal.sequence.support;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.sequence.SequenceConnector;

public class SequenceConnectorImpl implements SequenceConnector {
    private Map<String, SequenceRule> cache = new HashMap<String, SequenceRule>();
    private SequenceHelper sequenceHelper;
    private SequenceParser sequenceParser = new SequenceParser();

    public String generate(String code, String text) {
        return this.generate(code, text, new Date());
    }

    public String generate(String code, String text, Date date) {
        SequenceRule sequenceRule = this.cache.get(text);

        if (sequenceRule == null) {
            sequenceRule = sequenceParser.parse(text);
            cache.put(text, sequenceRule);
        }

        return sequenceRule.process(code, date, sequenceHelper);
    }

    @Resource
    public void setSequenceHelper(SequenceHelper sequenceHelper) {
        this.sequenceHelper = sequenceHelper;
    }
}
