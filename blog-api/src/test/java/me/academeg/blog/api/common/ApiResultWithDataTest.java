package me.academeg.blog.api.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * ApiResultWithDataTest
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public class ApiResultWithDataTest {

    @Test
    public void arbitraryResult() throws Exception {
        ApiResultWithData<ArbitraryResult<String>> result = new ApiResultWithData<>(
            10L,
            "unique msq",
            new ArbitraryResult<>("string result")
        );

        assertThat(result.getStatus()).isEqualTo(10);
        assertThat(result.getMessage()).isEqualTo("unique msq");
        assertThat(result.getData().getResult()).isEqualTo("string result");
    }

    @Test
    public void collectionResult() throws Exception {
        Collection<String> data = prepareCollection();

        ApiResultWithData<CollectionResult<String>> result = new ApiResultWithData<>(
            10L,
            "unique msq",
            new CollectionResult<>(data)
        );

        assertThat(result.getStatus()).isEqualTo(10);
        assertThat(result.getMessage()).isEqualTo("unique msq");

        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo(data.size());
        assertThat(result.getData().getResult()).containsExactly(
            "Moscow",
            "Saint. P.",
            "Murmansk"
        );
    }

    @Test
    public void collectionResultWithCustomTotalCount() throws Exception {
        Collection<String> data = prepareCollection();

        ApiResultWithData<CollectionResult<String>> result = new ApiResultWithData<>(
            10L,
            "unique msq",
            new CollectionResult<>(data, 100)
        );

        assertThat(result.getStatus()).isEqualTo(10);
        assertThat(result.getMessage()).isEqualTo("unique msq");

        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo(100L);
        assertThat(result.getData().getResult()).containsExactly(
            "Moscow",
            "Saint. P.",
            "Murmansk"
        );
    }

    @Test
    public void mapResult() throws Exception {
        Map<String, Integer> data = prepareMap();

        ApiResultWithData<MapResult<String, Integer>> result = new ApiResultWithData<>(
            10L,
            "unique msq",
            new MapResult<>(data)
        );

        assertThat(result.getStatus()).isEqualTo(10);
        assertThat(result.getMessage()).isEqualTo("unique msq");

        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo(data.size());
        assertThat(result.getData().getResult()).contains(
            entry("Saint. P.", 1),
            entry("Moscow", 2),
            entry("Murmansk", 3)
        );
    }

    @Test
    public void mapResultWithCustomTotalCount() throws Exception {
        Map<String, Integer> data = prepareMap();

        ApiResultWithData<MapResult<String, Integer>> result = new ApiResultWithData<>(
            10L,
            "unique msq",
            new MapResult<>(data, 100)
        );

        assertThat(result.getStatus()).isEqualTo(10);
        assertThat(result.getMessage()).isEqualTo("unique msq");

        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo(100L);
        assertThat(result.getData().getResult()).contains(
            entry("Saint. P.", 1),
            entry("Moscow", 2),
            entry("Murmansk", 3)
        );
    }

    private Collection<String> prepareCollection() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Moscow");
        data.add("Saint. P.");
        data.add("Murmansk");
        return data;
    }

    private Map<String, Integer> prepareMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Saint. P.", 1);
        map.put("Moscow", 2);
        map.put("Murmansk", 3);
        return map;
    }
}
