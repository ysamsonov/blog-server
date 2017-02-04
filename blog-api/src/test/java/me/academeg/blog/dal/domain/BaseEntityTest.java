package me.academeg.blog.dal.domain;

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BaseEntityTest
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 * @date 04.02.2017
 */
public class BaseEntityTest {

    @Test
    public void emptyConstructor() throws Exception {
        TestEntity entity = new TestEntity();
        assertThat(entity.getId()).isNotNull();
    }

    @Test
    public void idConstructorNull() throws Exception {
        TestEntity entity = new TestEntity(null);
        assertThat(entity.getId()).isNotNull();
    }

    @Test
    public void idConstructorWithId() throws Exception {
        UUID id = UUID.randomUUID();
        TestEntity entity = new TestEntity(id);
        assertThat(entity.getId()).isEqualTo(id);
    }

    static class TestEntity extends BaseEntity {
        public TestEntity() {
        }

        public TestEntity(UUID id) {
            super(id);
        }
    }
}
