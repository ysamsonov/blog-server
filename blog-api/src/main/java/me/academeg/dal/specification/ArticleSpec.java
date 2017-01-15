package me.academeg.dal.specification;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.annotations.QueryDelegate;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import me.academeg.dal.domain.Article;
import me.academeg.dal.domain.ArticleStatus;
import me.academeg.dal.domain.QArticle;

import java.util.UUID;

/**
 * ArticleSpec
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public class ArticleSpec {
    private static final QArticle article = QArticle.article;

    @QueryDelegate(Article.class)
    public static BooleanExpression withAuthorId(final QArticle article, final UUID authorId) {
        return article.author().id.eq(authorId);
    }

    public static BooleanExpression withAuthorId(final UUID authorId) {
        return withAuthorId(article, authorId);
    }

    @QueryDelegate(Article.class)
    public static BooleanExpression withStatus(final QArticle article, final ArticleStatus status) {
        return article.status.eq(status);
    }

    public static BooleanExpression withStatus(final ArticleStatus status) {
        return withStatus(article, status);
    }

    @QueryDelegate(Article.class)
    public static BooleanExpression hasTag(final QArticle article, final String tag) {
        return article.tags.any().value.equalsIgnoreCase(tag);
    }

    public static BooleanExpression hasTag(final String tag) {
        return hasTag(article, tag);
    }

    @QueryDelegate(Article.class)
    public static Predicate hasText(final QArticle article, final String text) {
        String expr = "%" + text + "%";
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(
            article.title.likeIgnoreCase(expr)
                .or(
                    article.text.likeIgnoreCase(expr))
        );
        builder.and(article.status.eq(ArticleStatus.PUBLISHED));
        return builder.getValue();
    }

    public static Predicate hasText(final String text) {
        return hasText(article, text);
    }
}
