package com.tomato.shine;

/**
 * @author yeshuxin on 17-1-10.
 */

public class ErrorFactory {
    public static ErrorType getDownloadFailError(String msg) {
        return new ErrorType(ErrorType.ERROR_CREATE_FILE, msg);

    }

    public static ErrorType getMd5CheckFailError(String msg) {
        return new ErrorType(ErrorType.ERROR_CHK_MD5, msg);
    }

    public static ErrorType getOtherType(String msg) {
        return new ErrorType(ErrorType.ERROR_OTHERS, msg);
    }
}
