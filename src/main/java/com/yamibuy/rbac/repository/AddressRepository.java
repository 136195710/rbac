package com.yamibuy.rbac.repository;

import com.yamibuy.rbac.domain.Address;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Address entity.
 */
@SuppressWarnings("unused")
public interface AddressRepository extends JpaRepository<Address,Long> {

    @Query("select distinct address from Address address left join fetch address.people")
    List<Address> findAllWithEagerRelationships();

    @Query("select address from Address address left join fetch address.people where address.id =:id")
    Address findOneWithEagerRelationships(@Param("id") Long id);

}
