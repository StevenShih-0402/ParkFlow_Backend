package application_operation.ParkFlow.entity;

import application_operation.ParkFlow.enums.ParkingRequestEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "PARKING_REQUEST")
public class ParkingRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String parkingSlotNumber;

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

    @Column(name = "CREATED_AT", nullable = false, updatable = false)//新增時間
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")//更新時間
    private LocalDateTime updatedAt;

    // 在新增資料前自動設定 CreatedAt 和 UpdatedAt
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 在更新資料前自動更新 UpdatedAt
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
