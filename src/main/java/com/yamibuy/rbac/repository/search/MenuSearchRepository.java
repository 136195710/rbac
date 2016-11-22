package com.yamibuy.rbac.repository.search;

import com.yamibuy.rbac.domain.Menu;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Menu entity.
 */
public interface MenuSearchRepository extends ElasticsearchRepository<Menu, Long> {
}
