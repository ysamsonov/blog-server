package me.academeg.service;

import me.academeg.entity.Comment;

import java.util.UUID;

/**
 * CommentService
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public interface CommentService {

    Comment add(Comment comment);

    void delete(UUID uuid);

    void delete(Comment comment);

    Comment getByUuid(UUID uuid);

    Comment edit(Comment comment);
}
