package me.academeg.common;

import lombok.Getter;

/**
 * ArbitraryResult
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Getter
public class ArbitraryResult<Result> implements ResultData {

    private final Result result;

    public ArbitraryResult(final Result result) {
        this.result = result;
    }
}
