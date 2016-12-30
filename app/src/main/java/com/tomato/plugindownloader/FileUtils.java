package com.tomato.plugindownloader;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author yeshuxin on 16-11-29.
 */

public class FileUtils {

    private static final String ROOT_DIR = "tomato";
    public static final String DOWNLOAD_SUFFIX = ".download";
    // get filename from the given url

    public static String getFileName(String url) {
        String fileName = null;
        if (!TextUtils.isEmpty(url)) {
            int index = url.indexOf("?");
            if (index != -1) {
                url = url.substring(0, index);
            }
            index = url.lastIndexOf("/");
            if (index != -1 && index < url.length() - 1) {
                fileName = url.substring(index + 1);
            }
        }
        return fileName;
    }

    public static File getRootDir() {

        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ROOT_DIR);
        if (!root.isDirectory()) {
            root.mkdirs();
        }
        return root;
    }

    public static String getRootPath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath() + ROOT_DIR;
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

    public static boolean deleteFile(String path, String name) {

        boolean result = false;
        File file = new File(path, name + DOWNLOAD_SUFFIX);
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }
}
