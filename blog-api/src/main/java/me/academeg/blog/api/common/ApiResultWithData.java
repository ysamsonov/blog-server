package me.academeg.blog.api.common;

import lombok.Getter;

/**
 * ApiResultWithData
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Getter
public class ApiResultWithData<DATA extends ResultData> extends ApiResultImpl {

    private final DATA data;

    public ApiResultWithData(final long status, final String message, final DATA data) {
        super(status, message);
        this.data = data;
    }
}
