package com.suryadeep.openshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_sequence")
    @SequenceGenerator(name = "image_sequence", sequenceName = "image_seq", initialValue = 1, allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String name;
    private String imageKey;
}
