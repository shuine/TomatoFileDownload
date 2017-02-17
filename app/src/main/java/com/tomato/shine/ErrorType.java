package com.tomato.shine;

import android.text.TextUtils;

/**
 * @author yeshuxin on 17-1-9.
 */

public class ErrorType {

    //创建文件失败
    public static final int ERROR_CREATE_FILE = 0x001;

    //不合法的参数
    public static final int ERROR_INVALID_PARAM = 0x002;

    //md5校验错误
    public static final int ERROR_CHK_MD5 = 0x003;

    //网络错误
    public static final int ERROR_NETWORK = 0x004;

    public static final int ERROR_OTHERS = 0x005;

    public int errorCode = ERROR_OTHERS;
    public String errorMsg = "";

    public ErrorType(int code, String msg) {
        this.errorCode = code;
        if (!TextUtils.isEmpty(msg)) {
            errorMsg = msg;
        }
    }
}
