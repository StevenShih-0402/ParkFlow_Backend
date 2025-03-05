package application_operation.ParkFlow.dao.parking;

import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.entity.ParkingRequestEntity;
import application_operation.ParkFlow.enums.ParkingRequestEnum;
import application_operation.ParkFlow.repository.ParkingRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@Log4j2
@RequiredArgsConstructor
public class ParkingDao {

    private final ParkingRequestRepository parkingRequestRepository;

    public ParkingRequestDto saveParkingRequest(ParkingRequestCreateDto parkingRequestCreateDto, UsersBaseDto usersBaseDto) {
        ParkingRequestEntity entity = new ParkingRequestEntity();
        entity.setWeekStartDate(parkingRequestCreateDto.getWeekStartDate());
        entity.setCellPhone(parkingRequestCreateDto.getCellPhone());
        entity.setCarNumber(parkingRequestCreateDto.getCarNumber());
        entity.setCarType(parkingRequestCreateDto.getCarType());
        entity.setStatus(ParkingRequestEnum.REVIEWING);
        entity.setApplicantId(usersBaseDto.getUserId());
        entity.setApplicationTime(LocalDateTime.now());

        parkingRequestRepository.save(entity);

        return ParkingRequestDto.builder()
                .weekStartDate(parkingRequestCreateDto.getWeekStartDate())
                .cellPhone(parkingRequestCreateDto.getCellPhone())
                .carNumber(parkingRequestCreateDto.getCarNumber())
                .carType(parkingRequestCreateDto.getCarType())
                .build();
    }
}
