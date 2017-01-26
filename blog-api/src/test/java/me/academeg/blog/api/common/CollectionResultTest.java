package me.academeg.blog.api.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CollectionResultTest
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public class CollectionResultTest {

    @Test
    public void resultTest() throws Exception {
        Collection<String> data = prepareCollection();

        CollectionResult<String> result = new CollectionResult<>(data);

        assertThat(result.getCount()).isEqualTo(data.size());
        assertThat(result.getTotal()).isEqualTo(data.size());
        assertThat(result.getResult()).isEqualTo(prepareCollection());
    }

    @Test
    public void resultTestWithCustomTotal() throws Exception {
        Collection<String> data = prepareCollection();

        CollectionResult<String> result = new CollectionResult<>(data, 100);

        assertThat(result.getCount()).isEqualTo(data.size());
        assertThat(result.getTotal()).isEqualTo(100L);
        assertThat(result.getResult()).isEqualTo(prepareCollection());
    }

    private Collection<String> prepareCollection() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Moscow");
        data.add("Murmansk");
        data.add("Saint. P.");
        return data;
    }
}
