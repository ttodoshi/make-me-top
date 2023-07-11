package org.example.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "rejection_reason", schema = "course")
@Data
public class RejectionReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reasonId;
    private String name;
}
