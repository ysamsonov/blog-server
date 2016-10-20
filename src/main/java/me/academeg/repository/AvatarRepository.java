package me.academeg.repository;

import me.academeg.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * AvatarRepository Repository
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
public interface AvatarRepository extends JpaRepository<Avatar, UUID> {
}
