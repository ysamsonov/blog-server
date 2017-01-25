package me.academeg.blog.api.common;

import lombok.Getter;

/**
 * ApiResultWithData
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Getter
public class ApiResultWithData extends ApiResultImpl {

    private final ResultData data;

    public ApiResultWithData(final long status, final String message, final ResultData data) {
        super(status, message);
        this.data = data;
    }
}
