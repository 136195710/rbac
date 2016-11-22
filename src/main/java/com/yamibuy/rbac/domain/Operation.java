package com.yamibuy.rbac.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Operation.
 */
@Entity
@Table(name = "operation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "operation")
public class Operation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "operation_name", nullable = false)
    private String operationName;

    @Column(name = "opera_desc")
    private String operaDesc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperationName() {
        return operationName;
    }

    public Operation operationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getOperaDesc() {
        return operaDesc;
    }

    public Operation operaDesc(String operaDesc) {
        this.operaDesc = operaDesc;
        return this;
    }

    public void setOperaDesc(String operaDesc) {
        this.operaDesc = operaDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Operation operation = (Operation) o;
        if(operation.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, operation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Operation{" +
            "id=" + id +
            ", operationName='" + operationName + "'" +
            ", operaDesc='" + operaDesc + "'" +
            '}';
    }
}
