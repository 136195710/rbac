package com.yamibuy.rbac.service;

import com.yamibuy.rbac.domain.Address;
import com.yamibuy.rbac.repository.AddressRepository;
import com.yamibuy.rbac.repository.search.AddressSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Address.
 */
@Service
@Transactional
public class AddressService {

    private final Logger log = LoggerFactory.getLogger(AddressService.class);
    
    @Inject
    private AddressRepository addressRepository;

    @Inject
    private AddressSearchRepository addressSearchRepository;

    /**
     * Save a address.
     *
     * @param address the entity to save
     * @return the persisted entity
     */
    public Address save(Address address) {
        log.debug("Request to save Address : {}", address);
        Address result = addressRepository.save(address);
        addressSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the addresses.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Address> findAll(Pageable pageable) {
        log.debug("Request to get all Addresses");
        Page<Address> result = addressRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one address by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Address findOne(Long id) {
        log.debug("Request to get Address : {}", id);
        Address address = addressRepository.findOneWithEagerRelationships(id);
        return address;
    }

    /**
     *  Delete the  address by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Address : {}", id);
        addressRepository.delete(id);
        addressSearchRepository.delete(id);
    }

    /**
     * Search for the address corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Address> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Addresses for query {}", query);
        Page<Address> result = addressSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
