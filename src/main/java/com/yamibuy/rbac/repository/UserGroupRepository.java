package com.yamibuy.rbac.repository;

import com.yamibuy.rbac.domain.UserGroup;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the UserGroup entity.
 */
@SuppressWarnings("unused")
public interface UserGroupRepository extends JpaRepository<UserGroup,Long> {

    @Query("select distinct userGroup from UserGroup userGroup left join fetch userGroup.roles")
    List<UserGroup> findAllWithEagerRelationships();

    @Query("select userGroup from UserGroup userGroup left join fetch userGroup.roles where userGroup.id =:id")
    UserGroup findOneWithEagerRelationships(@Param("id") Long id);

}
