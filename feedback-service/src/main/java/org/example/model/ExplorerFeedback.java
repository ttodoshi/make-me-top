package org.example.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "explorer_feedback")
@Data
public class ExplorerFeedback {
    private Integer keeperId;
    @Id
    private Integer explorerId;
    private Integer rating;
    @Column(length = 1000)
    private String comment;
}