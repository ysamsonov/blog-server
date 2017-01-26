package me.academeg.blog.api.common;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MapResultTest
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public class MapResultTest {

    @Test
    public void okMapResult() throws Exception {
        Map<String, Integer> data = prepareMap();

        ApiResultWithData<MapResult<String, Integer>> result = ResultFactory.build().ok(new MapResult<>(data));

        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData().getResult()).isEqualTo(prepareMap());
        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo(data.size());
    }

    @Test
    public void okMapResultWithCustomTotal() throws Exception {
        Map<String, Integer> data = prepareMap();

        ApiResultWithData<MapResult<String, Integer>> result = ResultFactory.build().ok(new MapResult<>(data, 100));

        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData().getResult()).isEqualTo(prepareMap());
        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo(100L);
    }

    private Map<String, Integer> prepareMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("Saint. P.", 1);
        map.put("Moscow", 2);
        map.put("Murmansk", 3);
        return map;
    }
}
