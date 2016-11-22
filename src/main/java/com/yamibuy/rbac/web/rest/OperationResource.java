package com.yamibuy.rbac.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.yamibuy.rbac.domain.Operation;

import com.yamibuy.rbac.repository.OperationRepository;
import com.yamibuy.rbac.repository.search.OperationSearchRepository;
import com.yamibuy.rbac.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Operation.
 */
@RestController
@RequestMapping("/api")
public class OperationResource {

    private final Logger log = LoggerFactory.getLogger(OperationResource.class);
        
    @Inject
    private OperationRepository operationRepository;

    @Inject
    private OperationSearchRepository operationSearchRepository;

    /**
     * POST  /operations : Create a new operation.
     *
     * @param operation the operation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new operation, or with status 400 (Bad Request) if the operation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/operations")
    @Timed
    public ResponseEntity<Operation> createOperation(@Valid @RequestBody Operation operation) throws URISyntaxException {
        log.debug("REST request to save Operation : {}", operation);
        if (operation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("operation", "idexists", "A new operation cannot already have an ID")).body(null);
        }
        Operation result = operationRepository.save(operation);
        operationSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/operations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("operation", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /operations : Updates an existing operation.
     *
     * @param operation the operation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated operation,
     * or with status 400 (Bad Request) if the operation is not valid,
     * or with status 500 (Internal Server Error) if the operation couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/operations")
    @Timed
    public ResponseEntity<Operation> updateOperation(@Valid @RequestBody Operation operation) throws URISyntaxException {
        log.debug("REST request to update Operation : {}", operation);
        if (operation.getId() == null) {
            return createOperation(operation);
        }
        Operation result = operationRepository.save(operation);
        operationSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("operation", operation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /operations : get all the operations.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of operations in body
     */
    @GetMapping("/operations")
    @Timed
    public List<Operation> getAllOperations() {
        log.debug("REST request to get all Operations");
        List<Operation> operations = operationRepository.findAll();
        return operations;
    }

    /**
     * GET  /operations/:id : get the "id" operation.
     *
     * @param id the id of the operation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the operation, or with status 404 (Not Found)
     */
    @GetMapping("/operations/{id}")
    @Timed
    public ResponseEntity<Operation> getOperation(@PathVariable Long id) {
        log.debug("REST request to get Operation : {}", id);
        Operation operation = operationRepository.findOne(id);
        return Optional.ofNullable(operation)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /operations/:id : delete the "id" operation.
     *
     * @param id the id of the operation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/operations/{id}")
    @Timed
    public ResponseEntity<Void> deleteOperation(@PathVariable Long id) {
        log.debug("REST request to delete Operation : {}", id);
        operationRepository.delete(id);
        operationSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("operation", id.toString())).build();
    }

    /**
     * SEARCH  /_search/operations?query=:query : search for the operation corresponding
     * to the query.
     *
     * @param query the query of the operation search 
     * @return the result of the search
     */
    @GetMapping("/_search/operations")
    @Timed
    public List<Operation> searchOperations(@RequestParam String query) {
        log.debug("REST request to search Operations for query {}", query);
        return StreamSupport
            .stream(operationSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
