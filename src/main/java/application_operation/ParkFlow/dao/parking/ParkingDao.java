package application_operation.ParkFlow.dao.parking;

import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.dto.parking.update.ParkingRequestUpdateDto;
import application_operation.ParkFlow.dto.parking.update.UpdateParkingRequestDto;
import application_operation.ParkFlow.entity.ParkingQuotaEntity;
import application_operation.ParkFlow.entity.ParkingRequestEntity;
import application_operation.ParkFlow.enums.ParkingRequestEnum;
import application_operation.ParkFlow.repository.ParkingQuotaRepository;
import application_operation.ParkFlow.repository.ParkingRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
@Log4j2
@RequiredArgsConstructor
public class ParkingDao {

    private final ParkingRequestRepository parkingRequestRepository;
    private final ParkingQuotaRepository pargetTotalSlotsRepository;

    public List<ParkingRequestEntity> findParkingRequestById (ParkingRequestUpdateDto parkingRequestUpdateDto) {

        return parkingRequestRepository.queryParkingRequestById(parkingRequestUpdateDto.getId());
    }

    public Boolean findParkingRequestByApplicantId(
            ParkingRequestCreateDto parkingRequestCreateDto,
            UsersBaseDto usersBaseDto
    ) {

        List<ParkingRequestEntity> parkingRequestEntities = parkingRequestRepository.queryParkingRequestByApplicantId(
                usersBaseDto.getUserId(),
                parkingRequestCreateDto.getWeekStartDate()
        );

        return parkingRequestEntities.isEmpty();
    }

    public Boolean findParkingRequestCheckQuota(ParkingRequestCreateDto parkingRequestCreateDto) {
        if(ObjectUtils.isEmpty(parkingRequestRepository)) {
            return false;
        }

        List<ParkingRequestEntity> currentRequest = parkingRequestRepository.getCurrentRequest(parkingRequestCreateDto.getWeekStartDate());
        List<ParkingQuotaEntity> totalSlots = pargetTotalSlotsRepository.getTotalSlots(parkingRequestCreateDto.getWeekStartDate());

        return currentRequest.size() < totalSlots.get(0).getTotalSlots();
    }

    public ParkingRequestDto saveParkingRequest(ParkingRequestCreateDto parkingRequestCreateDto, UsersBaseDto usersBaseDto) {
        ParkingRequestEntity entity = new ParkingRequestEntity();
        entity.setWeekStartDate(parkingRequestCreateDto.getWeekStartDate());
        entity.setCellPhone(parkingRequestCreateDto.getCellPhone());
        entity.setCarNumber(parkingRequestCreateDto.getCarNumber());
        entity.setCarType(parkingRequestCreateDto.getCarType());
        entity.setStatus(ParkingRequestEnum.REVIEWING);
        entity.setApplicantId(usersBaseDto.getUserId());
        entity.setApplicationTime(LocalDateTime.now());

        ParkingRequestEntity parkingRequest = parkingRequestRepository.save(entity);
        Integer id = parkingRequest.getId();

        return ParkingRequestDto.builder()
                .Id(id)
                .weekStartDate(parkingRequestCreateDto.getWeekStartDate())
                .cellPhone(parkingRequestCreateDto.getCellPhone())
                .carNumber(parkingRequestCreateDto.getCarNumber())
                .carType(parkingRequestCreateDto.getCarType())
                .build();
    }

    public UpdateParkingRequestDto updateParkingRequest(
            ParkingRequestEntity parkingRequestEntity,
            ParkingRequestUpdateDto parkingRequestUpdateDto,
            UsersBaseDto usersBaseDto
    ) {
        parkingRequestEntity.setParkingSlotNumber(parkingRequestUpdateDto.getParkingSlotNumber());
        parkingRequestEntity.setStatus(parkingRequestUpdateDto.getStatus());
        parkingRequestEntity.setReviewId(usersBaseDto.getUserId());
        parkingRequestEntity.setReviewTime(LocalDateTime.now());

        parkingRequestRepository.save(parkingRequestEntity);

        return UpdateParkingRequestDto.builder()
                .id(parkingRequestUpdateDto.getId())
                .parkingSlotNumber(parkingRequestUpdateDto.getParkingSlotNumber())
                .status(parkingRequestUpdateDto.getStatus())
                .build();
    }
}
