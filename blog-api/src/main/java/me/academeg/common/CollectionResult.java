package me.academeg.common;

import lombok.Getter;

import java.util.Collection;

/**
 * CollectionResult
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Getter
public class CollectionResult<T> extends ArbitraryResult<Collection<T>> {

    private final long total;
    private final int count;

    public CollectionResult(final Collection<T> result, final long total) {
        super(result);
        this.total = total;
        this.count = result.size();
    }
}
