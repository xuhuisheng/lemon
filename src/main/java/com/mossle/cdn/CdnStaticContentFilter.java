package com.mossle.cdn;

import com.mossle.core.servlet.StaticContentFilter;

public class CdnStaticContentFilter extends StaticContentFilter {
    public String baseDir;

    public CdnStaticContentFilter(String baseDir) {
        this.baseDir = baseDir;
    }

    public String findRealFilePath(String contentPath) {
        return baseDir + contentPath;
    }
}
