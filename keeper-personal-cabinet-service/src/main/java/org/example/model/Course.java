package org.example.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "course", schema = "course")
@Data
public class Course {
    @Id
    private Integer courseId;
    private String title;
    @CreatedDate
    private Date creationDate;
    @LastModifiedDate
    private Date lastModified;
    private String description;
}
