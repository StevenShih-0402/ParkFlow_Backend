package application_operation.ParkFlow.repository;

import application_operation.ParkFlow.entity.ParkingRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParkingRequestRepository extends JpaRepository<ParkingRequestEntity, Integer> {

    @Query(
            value = """
                    SELECT
                        *
                    FROM
                        PARKING_REQUEST
                    WHERE
                        APPLICANT_ID = :applicantId
                    AND
                        WEEK_START_DATE = :weekStartDate
                    AND
                        STATUS IN (0, 1)
                    """, nativeQuery = true
    )
    List<ParkingRequestEntity> queryParkingRequestByApplicantId(
            @Param("applicantId") Integer applicantId,
            @Param("weekStartDate") LocalDateTime weekStartDate
    );

    @Query(
            value = """
                    Select
                        *
                    FROM
                        PARKING_REQUEST
                    WHERE
                        WEEK_START_DATE = :weekStartDate
                    AND
                        STATUS IN (0, 1)
                    """, nativeQuery = true)
    List<ParkingRequestEntity> getCurrentRequest(@Param("weekStartDate")LocalDateTime weekStartDate);
}
