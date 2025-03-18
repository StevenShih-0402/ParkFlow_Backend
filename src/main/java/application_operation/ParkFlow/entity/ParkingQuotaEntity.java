package application_operation.ParkFlow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PARKING_QUOTA")
public class ParkingQuotaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parking_quota_seq_gen")
    @SequenceGenerator(name = "parking_quota_seq_gen", sequenceName = "SEQ_PARKING_QUOTA", allocationSize = 1)
    private Integer id;

    @Column(name = "WEEK_START_DATE", updatable = false)
    private LocalDateTime weekStartDate;

    @Column(name = "TOTAL_SLOTS")
    private Integer totalSlots = 1;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", insertable = false)
    private LocalDateTime updatedAt;
}
