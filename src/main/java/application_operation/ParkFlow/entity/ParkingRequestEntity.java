package application_operation.ParkFlow.entity;

import application_operation.ParkFlow.enums.ParkingRequestEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PARKING_REQUEST")
public class ParkingRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parking_request_seq_gen")
    @SequenceGenerator(name = "parking_request_seq_gen", sequenceName = "SEQ_PARKING_REQUEST", allocationSize = 1)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "WEEK_START_DATE")
    private LocalDateTime weekStartDate;

    @Column(name = "CELLPHONE")
    private String cellPhone;

    @Column(name = "CAR_NUMBER")
    private String carNumber;

    @Column(name = "CAR_TYPE")
    private String carType;

    @Column(name = "PARKING_SLOT_NUMBER")
    private Integer parkingSlotNumber;

    @Column(name = "STATUS")
    private ParkingRequestEnum status;

    @Column(name = "APPLICANT_ID")
    private Integer  applicantId;

    @Column(name = "APPLICATION_TIME")
    private LocalDateTime applicationTime;

    @Column(name = "REVIEW_ID")
    private Integer reviewId;

    @Column(name = "REVIEW_TIME")
    private LocalDateTime reviewTime;

    @Column(name = "CREATED_AT", updatable = false)//新增時間
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")//更新時間
    private LocalDateTime updatedAt;
}
