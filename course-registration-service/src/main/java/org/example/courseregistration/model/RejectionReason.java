package org.example.courseregistration.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "rejection_reason")
@Data
public class RejectionReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reasonId;
    @Column(nullable = false, unique = true)
    private String name;
}
