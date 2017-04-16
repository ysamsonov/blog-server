package me.academeg.blog.dal.service.impl;

import me.academeg.blog.api.exception.BlogEntityExistException;
import me.academeg.blog.api.exception.BlogEntityNotExistException;
import me.academeg.blog.dal.domain.Article;
import me.academeg.blog.dal.domain.ArticleStatus;
import me.academeg.blog.dal.domain.Tag;
import me.academeg.blog.dal.service.BaseServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 25.03.2017
 */
public class TagServiceImplTest extends BaseServiceTest {

    @Autowired
    private TagServiceImpl tagService;

    @Test
    public void create() throws Exception {
        Tag tag = new Tag();
        tag.setValue("cars");
        tag = tagService.create(tag);

        Tag savedTag = tagService.getById(tag.getId());
        assertThat(savedTag.getValue()).isEqualTo("cars");
        assertThat(savedTag.getArticles()).isEmpty();
    }

    @Test
    public void createTwice() throws Exception {
        Tag tag = new Tag().setValue("cars");
        tag = tagService.create(tag);

        Tag tag2 = new Tag().setValue("cars");
        tagService.create(tag2);

        List<Tag> tags = tagService.getPage(null).getContent();
        assertThat(tags.size()).isEqualTo(1);
        assertThat(tags).extracting(Tag::getValue).containsExactly("cars");
    }

    @Test
    public void update() throws Exception {
        Tag tag = new Tag().setValue("cars");
        tag = tagService.create(tag);

        Tag newTag = new Tag(tag.getId()).setValue("caaRS");
        tagService.update(newTag);

        Tag tagById = tagService.getById(newTag.getId());
        assertThat(tagById.getValue()).isEqualTo("caars");
    }

    @Test(expected = BlogEntityNotExistException.class)
    public void updateNotExist() throws Exception {
        Tag tag = new Tag().setValue("car");
        tagService.update(tag);
    }

    @Test(expected = BlogEntityExistException.class)
    public void updateWithExistValue() throws Exception {
        Tag tag1 = new Tag().setValue("car");
        tag1 = tagService.create(tag1);

        Tag tag2 = new Tag().setValue("caar");
        tag2 = tagService.create(tag2);

        Tag tagNew = new Tag(tag2.getId()).setValue("car");
        tagService.update(tagNew);
    }

    @Test
    public void getByValue() throws Exception {
        Tag tag = new Tag().setValue("Cars");
        tag = tagService.create(tag);

        Tag cars = tagService.getByValue("caRs");
        assertThat(cars).isNotNull();
        assertThat(cars.getId()).isEqualTo(tag.getId());
        assertThat(cars.getValue()).isEqualTo("cars");
    }

    @Test
    public void delete() throws Exception {
        Tag tag = new Tag().setValue("car");
        tag = tagService.create(tag);

        Article article = new Article();
        article.setTitle("Test title");
        article.setText("Test text");
        article.setCreationDate(new Date());
        article.setStatus(ArticleStatus.PUBLISHED);
        article.addTag(new Tag(tag.getId()));

        article = entityManager.merge(article);
        entityManager.flush();
        entityManager.clear();

        Tag savedTag = tagService.getById(tag.getId());
        assertThat(savedTag.getArticles().size() > 0).isTrue();

        Article savedArticle = entityManager.find(Article.class, article.getId());
        assertThat(savedArticle.getTags().size() > 0).isTrue();

        tagService.delete(tag.getId());

        List<Tag> content = tagService.getPage(null).getContent();
        assertThat(content).isEmpty();

        savedArticle = entityManager.find(Article.class, article.getId());
        assertThat(savedArticle.getTags()).isEmpty();
    }
}
