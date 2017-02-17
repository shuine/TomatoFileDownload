package com.tomato.shine;

import android.text.TextUtils;

/**
 * @author yeshuxin on 17-1-9.
 */

public class ErrorType {

    public static final int ERROR_DOWNLOAD_FAIL = 0x001;
    public static final int ERROR_MD5_FAIL = 0x002;
    public static final int ERROR_OTHERS = 0x003;

    public int errorCode = ERROR_OTHERS;
    public String errorMsg = "";

    public ErrorType(int code, String msg) {
        this.errorCode = code;
        if (!TextUtils.isEmpty(msg)) {
            errorMsg = msg;
        }
    }
}
