package com.yamibuy.rbac.repository;

import com.yamibuy.rbac.domain.Role;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Role entity.
 */
@SuppressWarnings("unused")
public interface RoleRepository extends JpaRepository<Role,Long> {

    @Query("select distinct role from Role role left join fetch role.operations")
    List<Role> findAllWithEagerRelationships();

    @Query("select role from Role role left join fetch role.operations where role.id =:id")
    Role findOneWithEagerRelationships(@Param("id") Long id);

}
