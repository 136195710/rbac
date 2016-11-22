package com.yamibuy.rbac.web.rest;

import com.yamibuy.rbac.RbacApp;

import com.yamibuy.rbac.domain.Operation;
import com.yamibuy.rbac.repository.OperationRepository;
import com.yamibuy.rbac.repository.search.OperationSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the OperationResource REST controller.
 *
 * @see OperationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RbacApp.class)
public class OperationResourceIntTest {

    private static final String DEFAULT_OPERATION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OPERATION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OPERA_DESC = "AAAAAAAAAA";
    private static final String UPDATED_OPERA_DESC = "BBBBBBBBBB";

    @Inject
    private OperationRepository operationRepository;

    @Inject
    private OperationSearchRepository operationSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restOperationMockMvc;

    private Operation operation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OperationResource operationResource = new OperationResource();
        ReflectionTestUtils.setField(operationResource, "operationSearchRepository", operationSearchRepository);
        ReflectionTestUtils.setField(operationResource, "operationRepository", operationRepository);
        this.restOperationMockMvc = MockMvcBuilders.standaloneSetup(operationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Operation createEntity(EntityManager em) {
        Operation operation = new Operation()
                .operationName(DEFAULT_OPERATION_NAME)
                .operaDesc(DEFAULT_OPERA_DESC);
        return operation;
    }

    @Before
    public void initTest() {
        operationSearchRepository.deleteAll();
        operation = createEntity(em);
    }

    @Test
    @Transactional
    public void createOperation() throws Exception {
        int databaseSizeBeforeCreate = operationRepository.findAll().size();

        // Create the Operation

        restOperationMockMvc.perform(post("/api/operations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(operation)))
                .andExpect(status().isCreated());

        // Validate the Operation in the database
        List<Operation> operations = operationRepository.findAll();
        assertThat(operations).hasSize(databaseSizeBeforeCreate + 1);
        Operation testOperation = operations.get(operations.size() - 1);
        assertThat(testOperation.getOperationName()).isEqualTo(DEFAULT_OPERATION_NAME);
        assertThat(testOperation.getOperaDesc()).isEqualTo(DEFAULT_OPERA_DESC);

        // Validate the Operation in ElasticSearch
        Operation operationEs = operationSearchRepository.findOne(testOperation.getId());
        assertThat(operationEs).isEqualToComparingFieldByField(testOperation);
    }

    @Test
    @Transactional
    public void checkOperationNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = operationRepository.findAll().size();
        // set the field null
        operation.setOperationName(null);

        // Create the Operation, which fails.

        restOperationMockMvc.perform(post("/api/operations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(operation)))
                .andExpect(status().isBadRequest());

        List<Operation> operations = operationRepository.findAll();
        assertThat(operations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOperations() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get all the operations
        restOperationMockMvc.perform(get("/api/operations?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(operation.getId().intValue())))
                .andExpect(jsonPath("$.[*].operationName").value(hasItem(DEFAULT_OPERATION_NAME.toString())))
                .andExpect(jsonPath("$.[*].operaDesc").value(hasItem(DEFAULT_OPERA_DESC.toString())));
    }

    @Test
    @Transactional
    public void getOperation() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);

        // Get the operation
        restOperationMockMvc.perform(get("/api/operations/{id}", operation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(operation.getId().intValue()))
            .andExpect(jsonPath("$.operationName").value(DEFAULT_OPERATION_NAME.toString()))
            .andExpect(jsonPath("$.operaDesc").value(DEFAULT_OPERA_DESC.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOperation() throws Exception {
        // Get the operation
        restOperationMockMvc.perform(get("/api/operations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOperation() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);
        operationSearchRepository.save(operation);
        int databaseSizeBeforeUpdate = operationRepository.findAll().size();

        // Update the operation
        Operation updatedOperation = operationRepository.findOne(operation.getId());
        updatedOperation
                .operationName(UPDATED_OPERATION_NAME)
                .operaDesc(UPDATED_OPERA_DESC);

        restOperationMockMvc.perform(put("/api/operations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedOperation)))
                .andExpect(status().isOk());

        // Validate the Operation in the database
        List<Operation> operations = operationRepository.findAll();
        assertThat(operations).hasSize(databaseSizeBeforeUpdate);
        Operation testOperation = operations.get(operations.size() - 1);
        assertThat(testOperation.getOperationName()).isEqualTo(UPDATED_OPERATION_NAME);
        assertThat(testOperation.getOperaDesc()).isEqualTo(UPDATED_OPERA_DESC);

        // Validate the Operation in ElasticSearch
        Operation operationEs = operationSearchRepository.findOne(testOperation.getId());
        assertThat(operationEs).isEqualToComparingFieldByField(testOperation);
    }

    @Test
    @Transactional
    public void deleteOperation() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);
        operationSearchRepository.save(operation);
        int databaseSizeBeforeDelete = operationRepository.findAll().size();

        // Get the operation
        restOperationMockMvc.perform(delete("/api/operations/{id}", operation.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean operationExistsInEs = operationSearchRepository.exists(operation.getId());
        assertThat(operationExistsInEs).isFalse();

        // Validate the database is empty
        List<Operation> operations = operationRepository.findAll();
        assertThat(operations).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOperation() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);
        operationSearchRepository.save(operation);

        // Search the operation
        restOperationMockMvc.perform(get("/api/_search/operations?query=id:" + operation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operation.getId().intValue())))
            .andExpect(jsonPath("$.[*].operationName").value(hasItem(DEFAULT_OPERATION_NAME.toString())))
            .andExpect(jsonPath("$.[*].operaDesc").value(hasItem(DEFAULT_OPERA_DESC.toString())));
    }
}
