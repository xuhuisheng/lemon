package com.mossle.core.export;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Exportor {
    void export(HttpServletRequest request, HttpServletResponse response,
            TableModel tableModel) throws IOException;
}
