package me.academeg.blog.api.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * ResultFactoryTest
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public class ResultFactoryTest {

    @Test
    public void ok() throws Exception {
        ApiResult result = ResultFactory.build().ok();

        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("OK");
    }

    @Test
    public void errorWithMsg() throws Exception {
        ApiResult result = ResultFactory.build().error("Fatal error");

        assertThat(result.getStatus()).isEqualTo(-1);
        assertThat(result.getMessage()).isEqualTo("Fatal error");
    }

    @Test
    public void errorWithStatusAndMsg() throws Exception {
        ApiResult result = ResultFactory.build().error(100, "Fatal error");

        assertThat(result.getStatus()).isEqualTo(100);
        assertThat(result.getMessage()).isEqualTo("Fatal error");
    }

    @Test
    public void okWithArbitraryResult() throws Exception {
        ApiResultWithData<ArbitraryResult<String>> result =
            ResultFactory
                .build()
                .ok(new ArbitraryResult<>("Some result"));

        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData().getResult()).isEqualTo("Some result");
    }

    @Test
    public void okCollectionResult() throws Exception {
        ArrayList<String> data = new ArrayList<>();
        data.add("Saint. P.");
        data.add("Moscow");
        data.add("Murmansk");

        ApiResultWithData<CollectionResult<String>> result = ResultFactory.build().ok(new CollectionResult<>(data));

        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData().getResult()).containsExactly("Saint. P.", "Moscow", "Murmansk");
        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo((long) data.size());
    }

    @Test
    public void okCollectionResultWithCustomTotal() throws Exception {
        ArrayList<String> data = new ArrayList<>();
        data.add("Saint. P.");
        data.add("Moscow");
        data.add("Murmansk");

        ApiResultWithData<CollectionResult<String>> result =
            ResultFactory
                .build()
                .ok(new CollectionResult<>(data, 100));

        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData().getResult()).containsExactly("Saint. P.", "Moscow", "Murmansk");
        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo(100);
    }

    @Test
    public void okMapResultWith() throws Exception {
        Map<String, Integer> data = new HashMap<>();
        data.put("Saint. P.", 1);
        data.put("Moscow", 2);
        data.put("Murmansk", 3);

        ApiResultWithData<MapResult<String, Integer>> result = ResultFactory.build().ok(new MapResult<>(data));

        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData().getResult())
            .contains(
                entry("Saint. P.", 1),
                entry("Moscow", 2),
                entry("Murmansk", 3)
            );
        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo((long) data.size());
    }

    @Test
    public void okMapResultWithCustomTotal() throws Exception {
        Map<String, Integer> data = new HashMap<>();
        data.put("Saint. P.", 1);
        data.put("Moscow", 2);
        data.put("Murmansk", 3);

        ApiResultWithData<MapResult<String, Integer>> result = ResultFactory.build().ok(new MapResult<>(data, 100));

        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData().getResult())
            .contains(
                entry("Saint. P.", 1),
                entry("Moscow", 2),
                entry("Murmansk", 3)
            );
        assertThat(result.getData().getCount()).isEqualTo(data.size());
        assertThat(result.getData().getTotal()).isEqualTo((long) 100);
    }
}
