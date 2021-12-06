package com.mossle.disk.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class FileUtils {
    /**
     * 获取文件后缀.
     */
    public static String getSuffix(String name) {
        if (name.indexOf(".") == -1) {
            return "";
        }

        String suffix = name.substring(name.lastIndexOf(".") + 1);

        return suffix.trim().toLowerCase();
    }

    /**
     * 获取文件前缀.
     */
    public static String getPrefix(String name) {
        if (name.indexOf(".") == -1) {
            return name;
        }

        String prefix = name.substring(0, name.indexOf("."));

        return prefix.trim();
    }

    /**
     * 文件改名.
     */
    public static String modifyFileName(String originalFileName,
            String appendPart) {
        String suffix = getSuffix(originalFileName);
        String prefix = getPrefix(originalFileName);

        if (StringUtils.isBlank(suffix)) {
            return prefix + appendPart;
        } else {
            return prefix + appendPart + "." + suffix;
        }
    }

    /**
     * 重名文件自动改名.
     */
    public static String calculateName(String name, List<String> checkedNames) {
        // System.out.println("name : " + name);
        // System.out.println("checkedNames : " + checkedNames);
        List<String> targetCheckedNames = new ArrayList<String>();

        name = name.trim();

        String targetPrefix = name;
        String targetSuffix = "";
        int index = name.lastIndexOf(".");

        if (index != -1) {
            targetPrefix = name.substring(0, index);
            targetSuffix = name.substring(index + 1);
        }

        String prefix = targetPrefix.toLowerCase();
        String suffix = null;

        if (targetSuffix.length() > 0) {
            suffix = "." + targetSuffix.toLowerCase();
        }

        for (String checkedName : checkedNames) {
            String targetCheckedName = checkedName.trim().toLowerCase();

            if (suffix != null) {
                if (targetCheckedName.endsWith(suffix)) {
                    targetCheckedNames.add(targetCheckedName.substring(0,
                            targetCheckedName.length() - suffix.length()));
                }
            } else {
                targetCheckedNames.add(targetCheckedName);
            }
        }

        // System.out.println("targetCheckedNames : " + targetCheckedNames);
        int count = 0;

        while (true) {
            boolean existsDumplicated = false;
            String currentName = prefix;

            if (count != 0) {
                currentName += ("(" + count + ")");
            }

            for (String checkedName : targetCheckedNames) {
                // System.out.println("checkedName : " + checkedName);
                // System.out.println("currentName : " + currentName);
                if (checkedName.equals(currentName)) {
                    existsDumplicated = true;

                    break;
                }
            }

            if (!existsDumplicated) {
                break;
            }

            count++;
        }

        if (count > 0) {
            targetPrefix = (targetPrefix + "(" + count + ")");
        }

        String targetName = targetPrefix;

        if (targetSuffix.length() > 0) {
            targetName += ("." + targetSuffix);
        }

        return targetName;
    }
}
