package com.mossle.portal.data;

import java.util.List;

public interface CsvCallback {
    void process(List<String> list, int lineNo) throws Exception;
}
