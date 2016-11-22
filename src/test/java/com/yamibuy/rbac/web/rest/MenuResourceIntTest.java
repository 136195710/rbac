package com.yamibuy.rbac.web.rest;

import com.yamibuy.rbac.RbacApp;

import com.yamibuy.rbac.domain.Menu;
import com.yamibuy.rbac.domain.Operation;
import com.yamibuy.rbac.repository.MenuRepository;
import com.yamibuy.rbac.repository.search.MenuSearchRepository;

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
 * Test class for the MenuResource REST controller.
 *
 * @see MenuResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RbacApp.class)
public class MenuResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ICON = "AAAAAAAAAA";
    private static final String UPDATED_ICON = "BBBBBBBBBB";

    private static final String DEFAULT_HREF = "AAAAAAAAAA";
    private static final String UPDATED_HREF = "BBBBBBBBBB";

    private static final String DEFAULT_PLAT = "AAAAAAAAAA";
    private static final String UPDATED_PLAT = "BBBBBBBBBB";

    private static final Integer DEFAULT_LEVEL = 1;
    private static final Integer UPDATED_LEVEL = 2;

    private static final Integer DEFAULT_PARENT_MENU_ID = 1;
    private static final Integer UPDATED_PARENT_MENU_ID = 2;

    @Inject
    private MenuRepository menuRepository;

    @Inject
    private MenuSearchRepository menuSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restMenuMockMvc;

    private Menu menu;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MenuResource menuResource = new MenuResource();
        ReflectionTestUtils.setField(menuResource, "menuSearchRepository", menuSearchRepository);
        ReflectionTestUtils.setField(menuResource, "menuRepository", menuRepository);
        this.restMenuMockMvc = MockMvcBuilders.standaloneSetup(menuResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Menu createEntity(EntityManager em) {
        Menu menu = new Menu()
                .name(DEFAULT_NAME)
                .icon(DEFAULT_ICON)
                .href(DEFAULT_HREF)
                .plat(DEFAULT_PLAT)
                .level(DEFAULT_LEVEL)
                .parentMenuId(DEFAULT_PARENT_MENU_ID);
        // Add required entity
        Operation operation = OperationResourceIntTest.createEntity(em);
        em.persist(operation);
        em.flush();
        menu.setOperation(operation);
        return menu;
    }

    @Before
    public void initTest() {
        menuSearchRepository.deleteAll();
        menu = createEntity(em);
    }

    @Test
    @Transactional
    public void createMenu() throws Exception {
        int databaseSizeBeforeCreate = menuRepository.findAll().size();

        // Create the Menu

        restMenuMockMvc.perform(post("/api/menus")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(menu)))
                .andExpect(status().isCreated());

        // Validate the Menu in the database
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeCreate + 1);
        Menu testMenu = menus.get(menus.size() - 1);
        assertThat(testMenu.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMenu.getIcon()).isEqualTo(DEFAULT_ICON);
        assertThat(testMenu.getHref()).isEqualTo(DEFAULT_HREF);
        assertThat(testMenu.getPlat()).isEqualTo(DEFAULT_PLAT);
        assertThat(testMenu.getLevel()).isEqualTo(DEFAULT_LEVEL);
        assertThat(testMenu.getParentMenuId()).isEqualTo(DEFAULT_PARENT_MENU_ID);

        // Validate the Menu in ElasticSearch
        Menu menuEs = menuSearchRepository.findOne(testMenu.getId());
        assertThat(menuEs).isEqualToComparingFieldByField(testMenu);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = menuRepository.findAll().size();
        // set the field null
        menu.setName(null);

        // Create the Menu, which fails.

        restMenuMockMvc.perform(post("/api/menus")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(menu)))
                .andExpect(status().isBadRequest());

        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIconIsRequired() throws Exception {
        int databaseSizeBeforeTest = menuRepository.findAll().size();
        // set the field null
        menu.setIcon(null);

        // Create the Menu, which fails.

        restMenuMockMvc.perform(post("/api/menus")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(menu)))
                .andExpect(status().isBadRequest());

        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkHrefIsRequired() throws Exception {
        int databaseSizeBeforeTest = menuRepository.findAll().size();
        // set the field null
        menu.setHref(null);

        // Create the Menu, which fails.

        restMenuMockMvc.perform(post("/api/menus")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(menu)))
                .andExpect(status().isBadRequest());

        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLevelIsRequired() throws Exception {
        int databaseSizeBeforeTest = menuRepository.findAll().size();
        // set the field null
        menu.setLevel(null);

        // Create the Menu, which fails.

        restMenuMockMvc.perform(post("/api/menus")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(menu)))
                .andExpect(status().isBadRequest());

        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMenus() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menu);

        // Get all the menus
        restMenuMockMvc.perform(get("/api/menus?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(menu.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].icon").value(hasItem(DEFAULT_ICON.toString())))
                .andExpect(jsonPath("$.[*].href").value(hasItem(DEFAULT_HREF.toString())))
                .andExpect(jsonPath("$.[*].plat").value(hasItem(DEFAULT_PLAT.toString())))
                .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
                .andExpect(jsonPath("$.[*].parentMenuId").value(hasItem(DEFAULT_PARENT_MENU_ID)));
    }

    @Test
    @Transactional
    public void getMenu() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menu);

        // Get the menu
        restMenuMockMvc.perform(get("/api/menus/{id}", menu.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(menu.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.icon").value(DEFAULT_ICON.toString()))
            .andExpect(jsonPath("$.href").value(DEFAULT_HREF.toString()))
            .andExpect(jsonPath("$.plat").value(DEFAULT_PLAT.toString()))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL))
            .andExpect(jsonPath("$.parentMenuId").value(DEFAULT_PARENT_MENU_ID));
    }

    @Test
    @Transactional
    public void getNonExistingMenu() throws Exception {
        // Get the menu
        restMenuMockMvc.perform(get("/api/menus/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMenu() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menu);
        menuSearchRepository.save(menu);
        int databaseSizeBeforeUpdate = menuRepository.findAll().size();

        // Update the menu
        Menu updatedMenu = menuRepository.findOne(menu.getId());
        updatedMenu
                .name(UPDATED_NAME)
                .icon(UPDATED_ICON)
                .href(UPDATED_HREF)
                .plat(UPDATED_PLAT)
                .level(UPDATED_LEVEL)
                .parentMenuId(UPDATED_PARENT_MENU_ID);

        restMenuMockMvc.perform(put("/api/menus")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedMenu)))
                .andExpect(status().isOk());

        // Validate the Menu in the database
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeUpdate);
        Menu testMenu = menus.get(menus.size() - 1);
        assertThat(testMenu.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMenu.getIcon()).isEqualTo(UPDATED_ICON);
        assertThat(testMenu.getHref()).isEqualTo(UPDATED_HREF);
        assertThat(testMenu.getPlat()).isEqualTo(UPDATED_PLAT);
        assertThat(testMenu.getLevel()).isEqualTo(UPDATED_LEVEL);
        assertThat(testMenu.getParentMenuId()).isEqualTo(UPDATED_PARENT_MENU_ID);

        // Validate the Menu in ElasticSearch
        Menu menuEs = menuSearchRepository.findOne(testMenu.getId());
        assertThat(menuEs).isEqualToComparingFieldByField(testMenu);
    }

    @Test
    @Transactional
    public void deleteMenu() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menu);
        menuSearchRepository.save(menu);
        int databaseSizeBeforeDelete = menuRepository.findAll().size();

        // Get the menu
        restMenuMockMvc.perform(delete("/api/menus/{id}", menu.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean menuExistsInEs = menuSearchRepository.exists(menu.getId());
        assertThat(menuExistsInEs).isFalse();

        // Validate the database is empty
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMenu() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menu);
        menuSearchRepository.save(menu);

        // Search the menu
        restMenuMockMvc.perform(get("/api/_search/menus?query=id:" + menu.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menu.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].icon").value(hasItem(DEFAULT_ICON.toString())))
            .andExpect(jsonPath("$.[*].href").value(hasItem(DEFAULT_HREF.toString())))
            .andExpect(jsonPath("$.[*].plat").value(hasItem(DEFAULT_PLAT.toString())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].parentMenuId").value(hasItem(DEFAULT_PARENT_MENU_ID)));
    }
}
