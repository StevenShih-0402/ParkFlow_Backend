package application_operation.ParkFlow.repository;

import application_operation.ParkFlow.entity.ParkingQuotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ParkingQuotaRepository extends JpaRepository<ParkingQuotaEntity, Integer> {

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
}
