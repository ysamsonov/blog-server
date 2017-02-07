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

    @Test
    public void hashTest() throws Exception {
        UUID id = UUID.randomUUID();
        TestEntity entity = new TestEntity(id);

        assertThat(entity.hashCode()).isEqualTo(id.hashCode());
    }

    @Test
    public void equalsAndHashCodeTest1() throws Exception {
        String id = "aacb6ad3-5465-4097-982f-5d1496ff34a1";
        TestEntity entity1 = new TestEntity(UUID.fromString(id));
        TestEntity entity2 = new TestEntity(UUID.fromString(id));

        assertThat(entity1.hashCode()==entity2.hashCode()).isTrue();
        assertThat(entity1.equals(entity2)).isTrue();
    }

    @Test
    public void equalsAndHashCodeTest2() throws Exception {
        String id1 = "aacb6ad3-5465-4097-982f-5d1496ff34a1";
        String id2 = "aacb6ad3-5465-4097-982f-5d1496ff34a2";
        TestEntity entity1 = new TestEntity(UUID.fromString(id1));
        TestEntity entity2 = new TestEntity(UUID.fromString(id2));

        assertThat(entity1.hashCode()==entity2.hashCode()).isFalse();
        assertThat(entity1.equals(entity2)).isFalse();
    }

    static class TestEntity extends BaseEntity {
        public TestEntity() {
        }

        public TestEntity(UUID id) {
            super(id);
        }
    }
}
