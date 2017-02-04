package me.academeg.blog.api.common;

/**
 * ResultFactory
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public class ResultFactory {

    private ResultFactory() {
    }

    public static ResultFactory build() {
        return new ResultFactory();
    }

    public ApiResult ok() {
        return new ApiResultImpl(0, "OK");
    }

    public <RESULT extends ResultData> ApiResultWithData<RESULT> ok(RESULT resultData) {
        return new ApiResultWithData<>(0, "OK", resultData);
    }

    public ApiResult error(String message) {
        return error(-1, message);
    }

    public ApiResult error(long status, String message) {
        return new ApiResultImpl(status, message);
    }
}
