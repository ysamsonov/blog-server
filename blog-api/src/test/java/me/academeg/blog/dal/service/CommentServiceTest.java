package me.academeg.blog.dal.service;

import me.academeg.blog.api.exception.BlogEntityNotExistException;
import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.domain.Article;
import me.academeg.blog.dal.domain.ArticleStatus;
import me.academeg.blog.dal.domain.Comment;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 09.05.2017
 */
public class CommentServiceTest extends BaseServiceTest {

    @Autowired
    private CommentService commentService;

    private UUID articleId;
    private UUID accountId;

    @Before
    public void setUp() throws Exception {
        Account account = new Account()
            .setEmail("yura@gmail.com")
            .setLogin("academeg")
            .setPassword("1234");
        accountId = entityManager.merge(account).getId();

        Article article = new Article()
            .setTitle("Title")
            .setText("Text")
            .setStatus(ArticleStatus.PUBLISHED)
            .setCreationDate(new Date())
            .setAuthor(new Account(accountId));
        articleId = entityManager.merge(article).getId();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void create() throws Exception {
        Comment comment = commentService.create(prepareEntity());

        assertThat(comment.getText()).isEqualTo("Super comment");
        assertThat(comment.getArticle().getId()).isEqualTo(articleId);
        assertThat(comment.getAuthor().getId()).isEqualTo(accountId);
        assertThat(comment.getCreationDate()).isNotNull();
    }

    @Test(expected = BlogEntityNotExistException.class)
    public void createForNullArticle() throws Exception {
        commentService.create(prepareEntity().setArticle(null));
    }

    @Test(expected = BlogEntityNotExistException.class)
    public void createForDraftArticle() throws Exception {
        Article article = new Article()
            .setTitle("Title")
            .setText("Text")
            .setStatus(ArticleStatus.DRAFT)
            .setCreationDate(new Date())
            .setAuthor(new Account(accountId));
        article = entityManager.merge(article);

        entityManager.flush();
        entityManager.clear();

        commentService.create(prepareEntity().setArticle(new Article(article.getId())));
    }

    @Test
    public void update() throws Exception {
        Comment comment = commentService.create(prepareEntity());

        Comment newComment = new Comment(comment.getId())
            .setText("Update super comment")
            .setAuthor(new Account(accountId))
            .setArticle(new Article(articleId))
            .setCreationDate(comment.getCreationDate());

        newComment = commentService.update(newComment);

        assertThat(newComment.getText()).isEqualTo("Update super comment");
        assertThat(newComment.getArticle().getId()).isEqualTo(articleId);
        assertThat(newComment.getAuthor().getId()).isEqualTo(accountId);
    }

    @Test
    public void delete() throws Exception {
        Comment comment = commentService.create(prepareEntity());
        commentService.delete(comment.getId());
        assertThat(commentService.getById(comment.getId())).isNull();
    }

    @Test
    public void getById() throws Exception {
        Comment comment = commentService.create(prepareEntity());
        comment = commentService.getById(comment.getId());
        assertThat(comment).isNotNull();
    }

    @Test
    public void getByArticleId() throws Exception {
        prepareListEntity().forEach(commentService::create);

        List<Comment> content = commentService.getPageByArticle(null, new Article(articleId)).getContent();
        assertThat(content.size()).isEqualTo(10);
    }

    private List<Comment> prepareListEntity() {
        ArrayList<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            comments.add(prepareEntity());
        }
        return comments;
    }

    private Comment prepareEntity() {
        return new Comment()
            .setText("Super comment")
            .setAuthor(new Account(accountId))
            .setArticle(new Article(articleId));
    }
}
