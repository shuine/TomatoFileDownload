package com.tomato.utils;

import java.io.File;
import java.io.IOException;

/**
 * @author yeshuxin on 16-11-1.
 */

public class FileUtil {

    public static File getNewFileName(String path, String name, String format) {
        String filePaht = path + name + format;
        int index = 0;
        File tempFile = new File(filePaht);
        while (tempFile.exists()) {
            index++;
            tempFile = new File(path + name + index + format);
        }
        return tempFile;
    }

    public static File createNewFile(File file) throws IOException {
        if (!file.exists()) {
            if (file.getParentFile() == null) {
                file.getParentFile().mkdirs();
            }
            File parentFile = new File(file.getParent());
            if (parentFile.exists()) {
                file.createNewFile();
            } else {
                boolean result = parentFile.mkdirs();
                if (result) {
                    file.createNewFile();
                }
            }
        }
        return file;
    }
}
