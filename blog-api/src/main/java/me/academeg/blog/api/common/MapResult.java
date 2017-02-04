package me.academeg.blog.api.common;

import lombok.Getter;

import java.util.Map;

/**
 * MapResult
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Getter
public class MapResult<K, V> extends ArbitraryResult<Map<K, V>> {

    private final long total;
    private final int count;

    public MapResult(final Map<K, V> result) {
        super(result);
        this.total = result.size();
        this.count = result.size();
    }

    public MapResult(final Map<K, V> result, final long total) {
        super(result);
        this.total = total;
        this.count = result.size();
    }
}
