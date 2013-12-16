package com.mossle.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestUtils {
    private static Logger logger = LoggerFactory.getLogger(RestUtils.class);
    public static final int ONE_SECOND = 1000;

    protected RestUtils() {
    }

    public static Response returnFile(File file, String ifModifiedSince) {
        if (!file.exists()) {
            return Response.status(Status.NOT_FOUND).build();
        }

        if (ifModifiedSince != null) {
            Date modifiedDate = null;

            Locale old = Locale.getDefault();
            Locale.setDefault(Locale.ENGLISH);

            try {
                modifiedDate = new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss zzz").parse(ifModifiedSince);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }

            Locale.setDefault(old);

            if ((modifiedDate != null)
                    && ((file.lastModified() - modifiedDate.getTime()) < ONE_SECOND)) {
                return Response.status(Status.NOT_MODIFIED).build();
            }
        }

        try {
            Date fileDate = new Date(file.lastModified());

            return Response.ok(new FileInputStream(file))
                    .lastModified(fileDate).build();
        } catch (FileNotFoundException ex) {
            logger.warn(ex.getMessage(), ex);

            return Response.status(Status.NOT_FOUND).build();
        }
    }
}
