package me.academeg.blog.api.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ArbitraryResultTest
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@RunWith(Parameterized.class)
public class ArbitraryResultTest {

    private String value;
    private ArbitraryResult<String> result;

    public ArbitraryResultTest(String value) {
        this.value = value;
        this.result = new ArbitraryResult<>(value);
    }

    @Parameters
    public static Collection<Object> data() {
        return Arrays.asList(new Object[]{
            "some string", "string", "Moscow"
        });
    }

    @Test
    public void resultTest() throws Exception {
        assertThat(result.getResult()).isEqualTo(value);
    }
}
