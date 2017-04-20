package me.academeg.blog.dal.service;

import me.academeg.blog.dal.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 19.04.2017
 */
public class ArticleServiceTest extends BaseServiceTest {

    @Autowired
    private ArticleService articleService;

    private UUID tagId;
    private UUID imageId;
    private UUID accountId;

    @Before
    public void setUp() throws Exception {
        Tag tag = new Tag().setValue("some tag");
        tagId = entityManager.merge(tag).getId();

        Image image = new Image().setOriginalPath("some_path").setThumbnailPath("other_path");
        imageId = entityManager.merge(image).getId();

        Account account = new Account().setEmail("yura@gmail.com").setLogin("academeg").setPassword("1234");
        accountId = entityManager.merge(account).getId();

        entityManager.flush();
    }

    @Test
    public void create() throws Exception {
        Article expected = prepareEntity();
        expected.addImage(new Image(imageId));

        Article article = prepareEntity();
        article.addImage(new Image(imageId));
        article = articleService.create(article);

        assertThat(article.getTitle()).isEqualTo(expected.getTitle());
        assertThat(article.getText()).isEqualTo(expected.getText());
        assertThat(article.getCreationDate()).isNotNull();
        assertThat(article.getStatus()).isEqualTo(expected.getStatus());
        assertThat(article.getAuthor().getId()).isEqualTo(expected.getAuthor().getId());
        assertThat(article.getCommentsCount()).isEqualTo(0);
        assertThat(article.getTags()).extracting(Tag::getId).containsExactly(tagId);
    }

    @Test
    public void createWithStatusDraft() throws Exception {
        Article article = prepareEntity();
        article.setStatus(ArticleStatus.DRAFT);

        article = articleService.create(article);
        assertThat(article.getStatus()).isEqualTo(ArticleStatus.DRAFT);
    }

    @Test
    public void createWithStatusLocked() throws Exception {
        Article article = prepareEntity();
        article.setStatus(ArticleStatus.LOCKED);

        article = articleService.create(article);
        assertThat(article.getStatus()).isNotEqualTo(ArticleStatus.LOCKED);
    }

    @Test
    public void edit() throws Exception {
        Article expected = modifyEntity(prepareEntity());

        Article article = articleService.create(prepareEntity());
        article = modifyEntity(article);
        article = articleService.update(article);

        assertThat(article.getTitle()).isEqualTo(expected.getTitle());
        assertThat(article.getText()).isEqualTo(expected.getText());
        assertThat(article.getCreationDate()).isNotNull();
        assertThat(article.getStatus()).isEqualTo(expected.getStatus());
        assertThat(article.getAuthor().getId()).isEqualTo(expected.getAuthor().getId());
        assertThat(article.getCommentsCount()).isEqualTo(0);
        assertThat(article.getTags()).extracting(Tag::getId).containsExactly(tagId);
    }

    @Test
    public void editLocked() throws Exception {
        Article article = articleService.create(prepareEntity());
        articleService.block(Collections.singleton(article.getId()));

        Article modifyArticle = prepareEntity().setStatus(ArticleStatus.PUBLISHED);
        modifyArticle.setId(article.getId());
        article = articleService.update(modifyArticle);

        assertThat(article.getStatus()).isEqualTo(ArticleStatus.LOCKED);
    }

    @Test
    public void editDraft() throws Exception {
        Article article = articleService.create(prepareEntity().setStatus(ArticleStatus.DRAFT));

        Article modifyArticle = prepareEntity().setStatus(ArticleStatus.PUBLISHED);
        modifyArticle.setId(article.getId());
        article = articleService.update(modifyArticle);

        assertThat(article.getStatus()).isEqualTo(ArticleStatus.PUBLISHED);
    }

    @Test
    public void block() throws Exception {
        prepareListEntity().forEach(articleService::create);

        List<Article> articles = articleService.getPage(null).getContent();
        articles
            .stream()
            .map(Article::getStatus)
            .forEach(status -> assertThat(status).isEqualTo(ArticleStatus.PUBLISHED));

        List<UUID> ids = articles.stream().map(Article::getId).collect(Collectors.toList());
        Random random = new Random(System.currentTimeMillis());
        ids.remove(random.nextInt(ids.size()));
        ids.remove(random.nextInt(ids.size()));
        ids.remove(random.nextInt(ids.size()));
        articleService.block(ids);

        articles = articleService.getPage(null).getContent();
        articles
            .stream()
            .filter(el -> ids.contains(el.getId()))
            .map(Article::getStatus)
            .forEach(status -> assertThat(status).isEqualTo(ArticleStatus.LOCKED));

        articles
            .stream()
            .filter(el -> !ids.contains(el.getId()))
            .map(Article::getStatus)
            .forEach(status -> assertThat(status).isEqualTo(ArticleStatus.PUBLISHED));
    }

    @Test
    public void blockDraftArticle() throws Exception {
        Article article = articleService.create(prepareEntity().setStatus(ArticleStatus.DRAFT));
        articleService.block(Collections.singleton(article.getId()));

        assertThat(article.getStatus()).isEqualTo(ArticleStatus.DRAFT);
    }

    @Test
    public void unlock() throws Exception {
        prepareListEntity().forEach(articleService::create);

        List<Article> articles = articleService.getPage(null).getContent();
        articles
            .stream()
            .map(Article::getStatus)
            .forEach(status -> assertThat(status).isEqualTo(ArticleStatus.PUBLISHED));

        List<UUID> ids = articles.stream().map(Article::getId).collect(Collectors.toList());
        Random random = new Random(System.currentTimeMillis());
        ids.remove(random.nextInt(ids.size()));
        ids.remove(random.nextInt(ids.size()));
        ids.remove(random.nextInt(ids.size()));
        articleService.block(ids);

        articleService.unlock(articles.stream().map(Article::getId).collect(Collectors.toList()));
        articles = articleService.getPage(null).getContent();
        articles
            .stream()
            .map(Article::getStatus)
            .forEach(status -> assertThat(status).isEqualTo(ArticleStatus.PUBLISHED));
    }

    @Test
    public void unlockDraftArticle() throws Exception {
        Article article = articleService.create(prepareEntity().setStatus(ArticleStatus.DRAFT));
        articleService.unlock(Collections.singleton(article.getId()));

        assertThat(article.getStatus()).isEqualTo(ArticleStatus.DRAFT);
    }

    // TODO: 19.04.2017 improve test use images, tags
    @Test
    public void delete() throws Exception {
        Article article = articleService.create(prepareEntity());
        article = articleService.getById(article.getId());

        articleService.delete(article.getId());

        article = articleService.getById(article.getId());
        assertThat(article).isNull();
    }

    private Article prepareEntity() {
        Article article = new Article();
        article.setTitle("Article title");
        article.setText("Article text");
        article.addTag(new Tag(tagId));
        article.setStatus(ArticleStatus.PUBLISHED);
        article.setAuthor(new Account(accountId));
        return article;
    }

    private List<Article> prepareListEntity() {
        ArrayList<Article> articles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            articles.add(prepareEntity());
        }
        return articles;
    }

    private Article modifyEntity(Article article) {
        article.setText("new text");
        article.setTitle("New Title");
        article.setStatus(ArticleStatus.DRAFT);
        return article;
    }
}
