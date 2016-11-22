package com.yamibuy.rbac.web.rest;

import com.yamibuy.rbac.RbacApp;

import com.yamibuy.rbac.domain.UserGroup;
import com.yamibuy.rbac.repository.UserGroupRepository;
import com.yamibuy.rbac.repository.search.UserGroupSearchRepository;

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
 * Test class for the UserGroupResource REST controller.
 *
 * @see UserGroupResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RbacApp.class)
public class UserGroupResourceIntTest {

    private static final String DEFAULT_GROUP_NAME = "AAAAAAAAAA";
    private static final String UPDATED_GROUP_NAME = "BBBBBBBBBB";

    @Inject
    private UserGroupRepository userGroupRepository;

    @Inject
    private UserGroupSearchRepository userGroupSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restUserGroupMockMvc;

    private UserGroup userGroup;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserGroupResource userGroupResource = new UserGroupResource();
        ReflectionTestUtils.setField(userGroupResource, "userGroupSearchRepository", userGroupSearchRepository);
        ReflectionTestUtils.setField(userGroupResource, "userGroupRepository", userGroupRepository);
        this.restUserGroupMockMvc = MockMvcBuilders.standaloneSetup(userGroupResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserGroup createEntity(EntityManager em) {
        UserGroup userGroup = new UserGroup()
                .groupName(DEFAULT_GROUP_NAME);
        return userGroup;
    }

    @Before
    public void initTest() {
        userGroupSearchRepository.deleteAll();
        userGroup = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserGroup() throws Exception {
        int databaseSizeBeforeCreate = userGroupRepository.findAll().size();

        // Create the UserGroup

        restUserGroupMockMvc.perform(post("/api/user-groups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userGroup)))
                .andExpect(status().isCreated());

        // Validate the UserGroup in the database
        List<UserGroup> userGroups = userGroupRepository.findAll();
        assertThat(userGroups).hasSize(databaseSizeBeforeCreate + 1);
        UserGroup testUserGroup = userGroups.get(userGroups.size() - 1);
        assertThat(testUserGroup.getGroupName()).isEqualTo(DEFAULT_GROUP_NAME);

        // Validate the UserGroup in ElasticSearch
        UserGroup userGroupEs = userGroupSearchRepository.findOne(testUserGroup.getId());
        assertThat(userGroupEs).isEqualToComparingFieldByField(testUserGroup);
    }

    @Test
    @Transactional
    public void checkGroupNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = userGroupRepository.findAll().size();
        // set the field null
        userGroup.setGroupName(null);

        // Create the UserGroup, which fails.

        restUserGroupMockMvc.perform(post("/api/user-groups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userGroup)))
                .andExpect(status().isBadRequest());

        List<UserGroup> userGroups = userGroupRepository.findAll();
        assertThat(userGroups).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUserGroups() throws Exception {
        // Initialize the database
        userGroupRepository.saveAndFlush(userGroup);

        // Get all the userGroups
        restUserGroupMockMvc.perform(get("/api/user-groups?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(userGroup.getId().intValue())))
                .andExpect(jsonPath("$.[*].groupName").value(hasItem(DEFAULT_GROUP_NAME.toString())));
    }

    @Test
    @Transactional
    public void getUserGroup() throws Exception {
        // Initialize the database
        userGroupRepository.saveAndFlush(userGroup);

        // Get the userGroup
        restUserGroupMockMvc.perform(get("/api/user-groups/{id}", userGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userGroup.getId().intValue()))
            .andExpect(jsonPath("$.groupName").value(DEFAULT_GROUP_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUserGroup() throws Exception {
        // Get the userGroup
        restUserGroupMockMvc.perform(get("/api/user-groups/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserGroup() throws Exception {
        // Initialize the database
        userGroupRepository.saveAndFlush(userGroup);
        userGroupSearchRepository.save(userGroup);
        int databaseSizeBeforeUpdate = userGroupRepository.findAll().size();

        // Update the userGroup
        UserGroup updatedUserGroup = userGroupRepository.findOne(userGroup.getId());
        updatedUserGroup
                .groupName(UPDATED_GROUP_NAME);

        restUserGroupMockMvc.perform(put("/api/user-groups")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedUserGroup)))
                .andExpect(status().isOk());

        // Validate the UserGroup in the database
        List<UserGroup> userGroups = userGroupRepository.findAll();
        assertThat(userGroups).hasSize(databaseSizeBeforeUpdate);
        UserGroup testUserGroup = userGroups.get(userGroups.size() - 1);
        assertThat(testUserGroup.getGroupName()).isEqualTo(UPDATED_GROUP_NAME);

        // Validate the UserGroup in ElasticSearch
        UserGroup userGroupEs = userGroupSearchRepository.findOne(testUserGroup.getId());
        assertThat(userGroupEs).isEqualToComparingFieldByField(testUserGroup);
    }

    @Test
    @Transactional
    public void deleteUserGroup() throws Exception {
        // Initialize the database
        userGroupRepository.saveAndFlush(userGroup);
        userGroupSearchRepository.save(userGroup);
        int databaseSizeBeforeDelete = userGroupRepository.findAll().size();

        // Get the userGroup
        restUserGroupMockMvc.perform(delete("/api/user-groups/{id}", userGroup.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean userGroupExistsInEs = userGroupSearchRepository.exists(userGroup.getId());
        assertThat(userGroupExistsInEs).isFalse();

        // Validate the database is empty
        List<UserGroup> userGroups = userGroupRepository.findAll();
        assertThat(userGroups).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchUserGroup() throws Exception {
        // Initialize the database
        userGroupRepository.saveAndFlush(userGroup);
        userGroupSearchRepository.save(userGroup);

        // Search the userGroup
        restUserGroupMockMvc.perform(get("/api/_search/user-groups?query=id:" + userGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].groupName").value(hasItem(DEFAULT_GROUP_NAME.toString())));
    }
}
