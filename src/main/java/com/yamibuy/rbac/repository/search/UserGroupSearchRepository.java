package com.yamibuy.rbac.repository.search;

import com.yamibuy.rbac.domain.UserGroup;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the UserGroup entity.
 */
public interface UserGroupSearchRepository extends ElasticsearchRepository<UserGroup, Long> {
}
