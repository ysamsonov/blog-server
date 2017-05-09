package me.academeg.blog.dal.service;

import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.domain.Avatar;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 22.04.2017
 */
public class AvatarServiceTest extends BaseServiceTest {

    private static final String TEST_IMAGE = "images/big_image.jpg";

    @Value("${me.academeg.blog.avatars.path}")
    private String path;

    @Autowired
    private AvatarService avatarService;

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File(path));
    }

    @Test
    public void create() throws Exception {
        UUID accountId = prepareAccount();
        MultipartFile multipartFile = prepareFile();

        Avatar avatar = avatarService.create(multipartFile, entityManager.find(Account.class, accountId));
        assertThat(avatar.getAccount()).isNotNull();
        assertThat(avatarService.getFile(avatar.getOriginalPath())).isNotEmpty();
        assertThat(avatarService.getFile(avatar.getThumbnailPath())).isNotEmpty();
        entityManager.clear();

        Account account = entityManager.find(Account.class, accountId);
        assertThat(account.getAvatar().getId()).isEqualTo(avatar.getId());
    }

    @Test
    public void createNewAndRemoveOld() throws Exception {
        UUID accountId = prepareAccount();
        MultipartFile oldMulFile = prepareFile();
        Avatar oldAvatar = avatarService.create(oldMulFile, entityManager.find(Account.class, accountId));
        entityManager.flush();
        entityManager.clear();

        MultipartFile newMulFile = prepareFile();
        Avatar newAvatar = avatarService.create(newMulFile, entityManager.find(Account.class, accountId));
        entityManager.flush();
        entityManager.clear();

        Account account = entityManager.find(Account.class, accountId);
        assertThat(account.getAvatar().getId()).isEqualTo(newAvatar.getId());

        assertThat(new File(path + oldAvatar.getThumbnailPath()).exists()).isFalse();
        assertThat(new File(path + oldAvatar.getOriginalPath()).exists()).isFalse();

        assertThat(new File(path + newAvatar.getThumbnailPath()).exists()).isTrue();
        assertThat(new File(path + newAvatar.getOriginalPath()).exists()).isTrue();
    }

    @Test
    public void delete() throws Exception {
        UUID accountId = prepareAccount();
        MultipartFile multipartFile = prepareFile();

        Avatar avatar = avatarService.create(multipartFile, entityManager.find(Account.class, accountId));
        avatarService.delete(avatar.getId());
        entityManager.flush();
        entityManager.clear();

        Account account = entityManager.find(Account.class, accountId);
        assertThat(account.getAvatar()).isNull();
    }

    private UUID prepareAccount() {
        Account account = new Account();
        account.setLogin("academeg");
        account.setEmail("yura@gmail.com");
        account.setPassword("1234");
        account = entityManager.merge(account);
        entityManager.flush();
        return account.getId();
    }

    private MultipartFile prepareFile() throws Exception {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito
            .when(multipartFile.getInputStream())
            .thenReturn(getResourceAsStream(TEST_IMAGE));

        return multipartFile;
    }

    private InputStream getResourceAsStream(String resourceName) {
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }
}
