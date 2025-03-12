package application_operation.ParkFlow.repository;

import application_operation.ParkFlow.entity.ParkingQuotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ParkingQuotaRepository extends JpaRepository<ParkingQuotaEntity, Integer> {

    ParkingQuotaEntity findByWeekStartDate(LocalDateTime weekStartDate);

    @Query(
            value = """
                    SELECT
                        *
                    FROM
                        PARKING_QUOTA
                    WHERE
                        WEEK_START_DATE = :weekStartDate
                    """, nativeQuery = true)
    List<ParkingQuotaEntity> getTotalSlots(@Param("weekStartDate") LocalDateTime weekStartDate);

    Boolean existsByWeekStartDate(LocalDateTime localDateTime);

    @Query(
            value = """
                    "SELECT
                        CASE WHEN COUNT(*) > 0
                            THEN 1
                            ELSE 0
                        END
                     FROM
                        PARKING_QUOTA
                     WHERE
                        WEEK_START_DATE = :localDateTime AND ID != :id"
                    """, nativeQuery = true)
    Boolean existsByWeekStartDateExceptSelf(Integer id, LocalDateTime localDateTime);
}
