package com.yamibuy.rbac.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Menu.
 */
@Entity
@Table(name = "menu")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "icon", nullable = false)
    private String icon;

    @NotNull
    @Column(name = "href", nullable = false)
    private String href;

    @Column(name = "plat")
    private String plat;

    @NotNull
    @Min(value = 1)
    @Max(value = 3)
    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "parent_menu_id")
    private Integer parentMenuId;

    @ManyToOne
    @NotNull
    private Operation operation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Menu name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public Menu icon(String icon) {
        this.icon = icon;
        return this;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHref() {
        return href;
    }

    public Menu href(String href) {
        this.href = href;
        return this;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getPlat() {
        return plat;
    }

    public Menu plat(String plat) {
        this.plat = plat;
        return this;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }

    public Integer getLevel() {
        return level;
    }

    public Menu level(Integer level) {
        this.level = level;
        return this;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getParentMenuId() {
        return parentMenuId;
    }

    public Menu parentMenuId(Integer parentMenuId) {
        this.parentMenuId = parentMenuId;
        return this;
    }

    public void setParentMenuId(Integer parentMenuId) {
        this.parentMenuId = parentMenuId;
    }

    public Operation getOperation() {
        return operation;
    }

    public Menu operation(Operation operation) {
        this.operation = operation;
        return this;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Menu menu = (Menu) o;
        if(menu.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, menu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Menu{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", icon='" + icon + "'" +
            ", href='" + href + "'" +
            ", plat='" + plat + "'" +
            ", level='" + level + "'" +
            ", parentMenuId='" + parentMenuId + "'" +
            '}';
    }
}
