package com.mossle.disk.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipProcessor {
    public TreeNode processTree(InputStream inputStream, String rootName)
            throws IOException {
        ZipInputStream zis = new ZipInputStream(inputStream);
        List<String> list = new ArrayList<String>();

        while (true) {
            ZipEntry ze = zis.getNextEntry();

            if (ze == null) {
                break;
            }

            // System.out.println(ze.getName());
            list.add(ze.getName());
        }

        zis.close();

        Collections.sort(list);

        TreeNode root = new TreeNode();
        root.setId("");
        root.setName(rootName);
        root.setType("folder");

        for (String text : list) {
            // System.out.println(text);
            String[] array = text.split("/");

            TreeNode current = root;
            String path = "";

            for (String part : array) {
                path += (part + "/");
                current = current.createOrFindChild(part);
                // System.out.println("part : " + part);
                current.setId(path);
            }

            if (!text.endsWith("/")) {
                current.setType("file");
                current.setId(current.getId().substring(0,
                        current.getId().length() - 1));
            }
        }

        return root;
    }

    public Map<String, Object> readEntry(InputStream inputStream, String path)
            throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        ZipInputStream zis = new ZipInputStream(inputStream);

        while (true) {
            ZipEntry ze = zis.getNextEntry();

            if (ze == null) {
                break;
            }

            System.out.println(ze.getName());

            // list.add(ze.getName());
            if (path.equals(ze.getName())) {
                System.out.println("hit : " + ze);
                resultMap.put("size", (int) ze.getSize());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] b = new byte[1024];

                while ((len = zis.read(b, 0, 1024)) != -1) {
                    baos.write(b, 0, len);
                }

                baos.flush();
                resultMap.put("bytes", baos.toByteArray());
            }
        }

        return resultMap;
    }
}
