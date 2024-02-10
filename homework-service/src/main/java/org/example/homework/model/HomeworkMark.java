package org.example.homework.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "homework_mark")
@Data
@NoArgsConstructor
public class HomeworkMark {
    @Id
    @Column(name = "request_id")
    private Long requestId;
    @OneToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false, insertable = false, updatable = false)
    @JsonBackReference
    @ToString.Exclude
    private HomeworkRequest request;
    @Column(columnDefinition = "TEXT")
    private String comment;

    public HomeworkMark(Long requestId, String comment) {
        this.requestId = requestId;
        this.comment = comment;
    }
}
