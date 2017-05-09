package me.academeg.blog.dal.service;

import me.academeg.blog.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 26.03.2017
 */
@Transactional
@Rollback
public abstract class BaseServiceTest extends BaseTest {

    @PersistenceContext
    protected EntityManager entityManager;
}
