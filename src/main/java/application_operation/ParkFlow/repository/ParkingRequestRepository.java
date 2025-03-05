package application_operation.ParkFlow.repository;

import application_operation.ParkFlow.entity.ParkingRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingRequestRepository extends JpaRepository<ParkingRequestEntity, Integer> {
}
