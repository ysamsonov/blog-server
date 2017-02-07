package me.academeg.blog.dal.service;

import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.domain.Avatar;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * AvatarService Service
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface AvatarService {

    Avatar create(MultipartFile file, Account account);

    Avatar getById(UUID id);

    void delete(UUID id);

    byte[] getFile(String name);
}
