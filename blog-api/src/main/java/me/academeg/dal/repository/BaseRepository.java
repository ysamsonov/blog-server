package me.academeg.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * BaseRepository
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@NoRepositoryBean
public interface BaseRepository<Entity, Id extends Serializable> extends JpaRepository<Entity, Id>, QueryDslPredicateExecutor<Entity> {
}
