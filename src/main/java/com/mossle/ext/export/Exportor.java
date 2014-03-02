package com.mossle.ext.export;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public interface Exportor {
    void export(HttpServletResponse response, TableModel tableModel)
            throws IOException;
}
