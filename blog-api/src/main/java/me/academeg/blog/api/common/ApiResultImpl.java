package me.academeg.blog.api.common;

import lombok.Getter;

/**
 * ApiResultImpl
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Getter
public class ApiResultImpl implements ApiResult {

    private long status;
    private String message;

    public ApiResultImpl() {
    }

    public ApiResultImpl(final long status, final String message) {
        this.status = status;
        this.message = message;
    }
}
