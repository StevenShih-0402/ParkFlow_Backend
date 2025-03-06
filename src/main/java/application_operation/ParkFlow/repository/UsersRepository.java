package application_operation.ParkFlow.repository;

import application_operation.ParkFlow.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Integer> {
    Boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
}
