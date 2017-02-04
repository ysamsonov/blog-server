package me.academeg.blog.api.common;

import lombok.Getter;

/**
 * ArbitraryResult
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Getter
public class ArbitraryResult<T> implements ResultData<T> {

    private final T result;

    public ArbitraryResult(final T result) {
        this.result = result;
    }
}
