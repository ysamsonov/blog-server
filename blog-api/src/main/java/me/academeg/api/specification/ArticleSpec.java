package me.academeg.api.specification;

import com.querydsl.core.annotations.QueryDelegate;
import com.querydsl.core.types.dsl.BooleanExpression;
import me.academeg.api.entity.Article;
import me.academeg.api.entity.ArticleStatus;
import me.academeg.api.entity.QArticle;

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

    static public BooleanExpression withAuthorId(final UUID authorId) {
        return article.author().id.eq(authorId);
    }

    @QueryDelegate(Article.class)
    public static BooleanExpression withStatus(final QArticle article, final ArticleStatus status) {
        return article.status.eq(status);
    }

    static public BooleanExpression withStatus(final ArticleStatus status) {
        return article.status.eq(status);
    }

    @QueryDelegate(Article.class)
    public static BooleanExpression hasTag(final QArticle article, final String tag) {
        return article.tags.any().value.eq(tag);
    }

    static public BooleanExpression hasTag(final String tag) {
        return article.tags.any().value.eq(tag);
    }
}
