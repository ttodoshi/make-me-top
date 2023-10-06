package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "homework_mark")
@Data
@NoArgsConstructor
public class HomeworkMark {
    @Id
    @Column(name = "request_id")
    private Integer requestId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private HomeworkRequest request;
    private Integer mark;
    @Column(columnDefinition = "TEXT")
    private String comment;

    public HomeworkMark(Integer requestId, Integer mark, String comment) {
        this.requestId = requestId;
        this.mark = mark;
        this.comment = comment;
    }
}
