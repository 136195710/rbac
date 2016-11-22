package com.yamibuy.rbac.repository;

import com.yamibuy.rbac.domain.Operation;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Operation entity.
 */
@SuppressWarnings("unused")
public interface OperationRepository extends JpaRepository<Operation,Long> {

}
