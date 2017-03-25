package me.academeg.blog.dal.service.impl;

import me.academeg.blog.BaseTest;
import me.academeg.blog.dal.domain.Tag;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 25.03.2017
 */
@Transactional
@Rollback
public class TagServiceImplTest extends BaseTest {

    @Autowired
    private TagServiceImpl tagService;

    @Test
    public void create() throws Exception {
        Tag tag = new Tag();
        tag.setValue("cars");

        tagService.create(tag);

        List<Tag> tags = tagService.getPage(null).getContent();

        assertThat(tags.size()).isEqualTo(1);
        assertThat(tags).extracting(Tag::getValue).containsExactly("cars");
    }

    @Test
    public void createTwice() throws Exception {
        Tag tag = new Tag().setValue("cars");
        Tag tag2 = new Tag().setValue("cars");

        tagService.create(tag);
        tagService.create(tag2);

        List<Tag> tags = tagService.getPage(null).getContent();

        assertThat(tags.size()).isEqualTo(1);
        assertThat(tags).extracting(Tag::getValue).containsExactly("cars");
    }
}
