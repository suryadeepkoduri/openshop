package com.suryadeep.openshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_sequence")
    @SequenceGenerator(name = "category_sequence", sequenceName = "category_seq", initialValue = 1, allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String name;
    @ManyToMany
    @JoinTable(
            name = "category_images",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<Image> images  = new ArrayList<>();
    private String description;
}
