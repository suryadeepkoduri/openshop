package com.suryadeep.openshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "roles")
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_sequence")
    @SequenceGenerator(name = "role_sequence", sequenceName = "role_seq", initialValue = 1, allocationSize = 1)
    @Column(nullable = false, updatable = false,name = "role_id")
    private Long roleId;
    private String roleName;
}
