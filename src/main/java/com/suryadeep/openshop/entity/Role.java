package com.suryadeep.openshop.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "roles", indexes = {
    @Index(name = "idx_role_name", columnList = "roleName")
})
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_sequence")
    @SequenceGenerator(name = "role_sequence", sequenceName = "role_seq", initialValue = 1, allocationSize = 1)
    @Column(nullable = false, updatable = false,name = "role_id")
    private Long roleId;
    private String roleName;
}
