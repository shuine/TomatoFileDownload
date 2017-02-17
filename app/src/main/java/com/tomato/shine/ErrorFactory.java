package com.tomato.shine;

/**
 * @author yeshuxin on 17-1-10.
 */

public class ErrorFactory {
    public static ErrorType getDownloadFailError(String msg) {
        return new ErrorType(ErrorType.ERROR_DOWNLOAD_FAIL, msg);

    }

    public static ErrorType getMd5CheckFailError(String msg) {
        return new ErrorType(ErrorType.ERROR_MD5_FAIL, msg);
    }

    public static ErrorType getOtherType(String msg) {
        return new ErrorType(ErrorType.ERROR_OTHERS, msg);
    }
}
