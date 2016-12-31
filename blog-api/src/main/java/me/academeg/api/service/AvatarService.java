package me.academeg.api.service;

import me.academeg.api.entity.Account;
import me.academeg.api.entity.Avatar;
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
}
