package com.mossle.core.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvProcessor {
    private static Logger logger = LoggerFactory.getLogger(CsvProcessor.class);

    public void process(String dataFilePath, String dataFileEncoding,
            CsvCallback csvCallback) throws Exception {
        InputStream is = CsvProcessor.class.getClassLoader()
                .getResourceAsStream(dataFilePath);

        if (is == null) {
            logger.info("cannot find : {}", dataFilePath);

            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                dataFileEncoding));

        String line = null;
        int lineNo = 0;

        while ((line = reader.readLine()) != null) {
            lineNo++;

            if (lineNo == 1) {
                continue;
            }

            try {
                this.processLine(line, lineNo, csvCallback);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public void processLine(String line, int lineNo, CsvCallback csvCallback)
            throws Exception {
        List<String> list = new ArrayList<String>();
        StringBuilder buff = new StringBuilder();

        boolean quote = false;

        for (int i = 0, len = line.length(); i < len; i++) {
            char c = line.charAt(i);

            switch (c) {
            case ',':

                if (!quote) {
                    list.add(this.processItem(buff.toString()));
                    buff = new StringBuilder();

                    continue;
                }

                break;

            case '"':
                quote = !quote;

                break;

            default:
            }

            buff.append(c);
        }

        if (buff.length() > 0) {
            list.add(this.processItem(buff.toString()));
        }

        csvCallback.process(list, lineNo);
    }

    public String processItem(String text) {
        if (text == null) {
            logger.info("text is null");

            return "";
        }

        text = text.trim();

        if (text.charAt(0) == '\"') {
            text = text.substring(1);
        }

        if (text.charAt(text.length() - 1) == '\"') {
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }
}
