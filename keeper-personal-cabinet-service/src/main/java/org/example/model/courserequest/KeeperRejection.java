package org.example.model.courserequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "keeper_rejection", schema = "course")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeeperRejection {
    @Id
    private Integer responseId;
    @JoinColumn(name = "reason_id", table = "rejection_reason")
    private Integer reasonId;
}
