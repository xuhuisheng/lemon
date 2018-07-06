package com.mossle.auth.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvParser {
    private static Logger logger = LoggerFactory.getLogger(CsvParser.class);
    private List<String[]> list = new ArrayList<String[]>();

    public List<String[]> parse(String filePath, String encoding)
            throws Exception {
        InputStream is = CsvParser.class.getClassLoader().getResourceAsStream(
                filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                encoding));

        String line = null;
        int lineNo = 0;

        while ((line = reader.readLine()) != null) {
            lineNo++;

            if (lineNo == 1) {
                continue;
            }

            this.processLine(line, lineNo);
        }

        return list;
    }

    public void processLine(String line, int lineNo) {
        String[] array = line.split(",\"");
        String[] targetArray = new String[array.length];

        for (int i = 0; i < array.length; i++) {
            targetArray[i] = this.processItem(array[i]);
        }

        list.add(targetArray);
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
