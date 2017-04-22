package me.academeg.blog.dal.service;

import me.academeg.blog.dal.domain.Article;
import me.academeg.blog.dal.domain.ArticleStatus;
import me.academeg.blog.dal.domain.Image;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 22.04.2017
 */
public class ImageServiceTest extends BaseServiceTest {

    private static final String TEST_IMAGE = "images/big_image.jpg";

    @Value("${me.academeg.blog.images.path}")
    private String path;

    @Autowired
    private ImageService imageService;

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File(path));
    }

    @Test
    public void create() throws Exception {
        MultipartFile multipartFile = prepareFile();
        Image image = imageService.create(multipartFile);
        image = imageService.getById(image.getId());

        assertThat(image.getArticle()).isNull();
        assertThat(imageService.getFile(image.getOriginalPath())).isNotEmpty();
        assertThat(imageService.getFile(image.getThumbnailPath())).isNotEmpty();
    }

    @Test
    public void update() throws Exception {
        MultipartFile multipartFile = prepareFile();
        Image image = imageService.create(multipartFile);
        image = imageService.getById(image.getId());

        UUID articleId = createArticle();
        Image updatedImage = new Image(image.getId());
        updatedImage.setOriginalPath(image.getOriginalPath());
        updatedImage.setThumbnailPath(image.getThumbnailPath());
        updatedImage.setArticle(new Article(articleId));
        image = imageService.update(updatedImage);

        assertThat(image.getArticle().getId()).isEqualTo(articleId);
    }

    @Test
    public void delete() throws Exception {
        MultipartFile multipartFile = prepareFile();
        Image image = imageService.create(multipartFile);
        image = imageService.getById(image.getId());

        UUID articleId = createArticle();
        Image updatedImage = new Image(image.getId());
        updatedImage.setOriginalPath(image.getOriginalPath());
        updatedImage.setThumbnailPath(image.getThumbnailPath());
        updatedImage.setArticle(new Article(articleId));
        image = imageService.update(updatedImage);

        imageService.delete(image.getId());

        assertThat(imageService.getById(image.getId())).isNull();
        Article article = entityManager.find(Article.class, articleId);
        assertThat(article.getImages()).isEmpty();
    }

    private MultipartFile prepareFile() throws Exception {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito
            .when(multipartFile.getInputStream())
            .thenReturn(getResourceAsStream(TEST_IMAGE));

        return multipartFile;
    }

    private UUID createArticle() {
        Article article = new Article();
        article.setText("super text");
        article.setTitle("super title");
        article.setCreationDate(new Date());
        article.setStatus(ArticleStatus.PUBLISHED);
        article = entityManager.merge(article);
        entityManager.flush();
        return article.getId();
    }

    private InputStream getResourceAsStream(String resourceName) {
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }
}
