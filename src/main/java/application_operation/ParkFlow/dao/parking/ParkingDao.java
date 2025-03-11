package application_operation.ParkFlow.dao.parking;

import application_operation.ParkFlow.controller.parking.payload.QueryUserParkingRequestRq;
import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.parking.create.ParkingQuotaCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.QueryUserParkingRequestDto;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;


@Component
@Log4j2
@RequiredArgsConstructor
public class ParkingDao {

    private final ParkingRequestRepository parkingRequestRepository;
    private final ParkingQuotaRepository parkingQuotaRepository;

    public List<ParkingRequestEntity> findParkingRequestById (ParkingRequestUpdateDto parkingRequestUpdateDto) {

        return parkingRequestRepository.queryParkingRequestById(parkingRequestUpdateDto.getId());
    }

    public Boolean findParkingRequestByApplicantId(
            ParkingRequestCreateDto parkingRequestCreateDto,
            UsersBaseDto usersBaseDto
    ) {

        List<ParkingRequestEntity> parkingRequestEntities = parkingRequestRepository.queryParkingRequestByApplicantId(
                usersBaseDto.getUserId(),
                parkingRequestCreateDto.getNextWeekStartDate()
        );

        return parkingRequestEntities.isEmpty();
    }

    public Boolean findParkingRequestCheckQuota(ParkingRequestCreateDto parkingRequestCreateDto) {
        if(ObjectUtils.isEmpty(parkingRequestRepository)) {
            return false;
        }

        List<ParkingRequestEntity> currentRequest = parkingRequestRepository.getCurrentRequest(parkingRequestCreateDto.getNextWeekStartDate());
        List<ParkingQuotaEntity> totalSlots = parkingQuotaRepository.getTotalSlots(parkingRequestCreateDto.getNextWeekStartDate());

        return currentRequest.size() < totalSlots.get(0).getTotalSlots();
    }

    public ParkingRequestDto saveParkingRequest(ParkingRequestCreateDto parkingRequestCreateDto, UsersBaseDto usersBaseDto) {
        ParkingRequestEntity entity = new ParkingRequestEntity();
        LocalDateTime applicationTime = LocalDateTime.now();

        entity.setWeekStartDate(parkingRequestCreateDto.getNextWeekStartDate());
        entity.setCellPhone(parkingRequestCreateDto.getCellPhone());
        entity.setCarNumber(parkingRequestCreateDto.getCarNumber());
        entity.setCarType(parkingRequestCreateDto.getCarType());
        entity.setStatus(ParkingRequestEnum.REVIEW);
        entity.setApplicantId(usersBaseDto.getUserId());
        entity.setApplicationTime(applicationTime);

        ParkingRequestEntity parkingRequest = parkingRequestRepository.save(entity);
        Integer id = parkingRequest.getId();

        return ParkingRequestDto.builder()
                .Id(id)
                .weekStartDate(parkingRequestCreateDto.getNextWeekStartDate())
                .cellPhone(parkingRequestCreateDto.getCellPhone())
                .carNumber(parkingRequestCreateDto.getCarNumber())
                .carType(parkingRequestCreateDto.getCarType())
                .applicationTime(applicationTime)
                .build();
    }

    public ParkingQuotaEntity saveParkingQuota(ParkingQuotaCreateDto parkingQuotaCreateDto){
        ParkingQuotaEntity parkingQuotaEntity = new ParkingQuotaEntity();
        parkingQuotaEntity.setWeekStartDate(parkingQuotaCreateDto.getWeekStartDate());
        parkingQuotaEntity.setTotalSlots(parkingQuotaCreateDto.getTotalSlots());

        return parkingQuotaRepository.save(parkingQuotaEntity);
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

    public QueryUserParkingRequestDto queryUserParkingRequest(QueryUserParkingRequestRq queryUserParkingRequestRq, UsersBaseDto usersBaseDto) {
        List<Object[]> parkingRequestAndUsers = parkingRequestRepository.queryUserParkingRequest(
                queryUserParkingRequestRq.getWeekStartDate(),
                usersBaseDto.getUserId()
        );

        List<QueryUserParkingRequestDto.parkingRequest> parkingRequestList = parkingRequestAndUsers.stream().map(dto -> {
            QueryUserParkingRequestDto.parkingRequest parkingRequest = new QueryUserParkingRequestDto.parkingRequest();

            parkingRequest.setRequestTime(dto[0] instanceof Timestamp ? ((Timestamp) dto[0]).toLocalDateTime() : null);
            parkingRequest.setChineseName(dto[1] != null ? dto[1].toString() : "");
            parkingRequest.setCarType(dto[2] != null ? dto[2].toString() : "");
            parkingRequest.setCarNumber(maskCarNumber(dto[3] != null ? dto[3].toString() : ""));
            parkingRequest.setCellphone(maskCellphone(dto[4] != null ? dto[4].toString() : ""));
            parkingRequest.setParkingSlotNumber(dto[5] instanceof Number ? ((Number) dto[5]).intValue() : null);
            parkingRequest.setStatus(dto[6] != null ? ParkingRequestEnum.getNameByCode(dto[6].toString()) : "");

            return parkingRequest;
        }).toList();

        return new QueryUserParkingRequestDto(parkingRequestList);
    }

    /**
     * 車牌號碼打碼，只保留第一碼與最後一碼，其餘以 '*' 取代
     */
    private String maskCarNumber(String carNumber) {
        if (carNumber == null || carNumber.isEmpty()) {
            return "";
        }
        // 找到 `-` 符號的位置
        int dashIndex = carNumber.indexOf('-');

        if (dashIndex <= 0 || dashIndex >= carNumber.length() - 1) {
            // 若無 `-` 或格式異常，則回傳原始車牌
            return carNumber;
        }

        String firstChar = carNumber.substring(0, 1); // 第一個字母
        String lastChar = carNumber.substring(carNumber.length() - 1); // 最後一碼
        String maskedMiddle = "*".repeat(dashIndex - 1) + "-" + "*".repeat(carNumber.length() - dashIndex - 2);

        return firstChar + maskedMiddle + lastChar;
    }

    /**
     * 手機號碼打碼，僅保留前三碼與最後一碼，其餘以 '*' 取代
     */
    private String maskCellphone(String cellphone) {
        if (cellphone == null || cellphone.length() < 4) {
            return cellphone; // 若號碼長度過短，則不做遮蔽
        }
        return cellphone.substring(0, 3) + "*".repeat(cellphone.length() - 4) + cellphone.charAt(cellphone.length() - 1);
    }
}
