package me.academeg.blog.api.common;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ApiResultImplTest
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public class ApiResultImplTest {

    private final ApiResultImpl result = new ApiResultImpl(10, "cool msg");

    @Test
    public void getStatus() throws Exception {
        assertThat(result.getStatus()).isEqualTo(10);
    }

    @Test
    public void getMessage() throws Exception {
        assertThat(result.getMessage()).isEqualTo("cool msg");
    }
}
