package application_operation.ParkFlow.dao.parking;

import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.parking.create.ParkingQuotaCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryParkingRequest.QueryParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.QueryUserParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryParkingRequest.ReParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.ReUserParkingRequestDto;
import application_operation.ParkFlow.dto.parking.update.ParkingQuotaUpdateDto;
import application_operation.ParkFlow.dto.parking.update.ParkingRequestUpdateDto;
import application_operation.ParkFlow.dto.parking.update.UpdateParkingRequestDto;
import application_operation.ParkFlow.entity.ParkingQuotaEntity;
import application_operation.ParkFlow.entity.ParkingRequestEntity;
import application_operation.ParkFlow.enums.ParkingRequestEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.repository.ParkingQuotaRepository;
import application_operation.ParkFlow.repository.ParkingRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
@Log4j2
@RequiredArgsConstructor
public class ParkingDao {

    private final ParkingRequestRepository parkingRequestRepository;
    private final ParkingQuotaRepository parkingQuotaRepository;

    @Transactional(readOnly = true)
    public List<ParkingRequestEntity> findParkingRequestById (ParkingRequestUpdateDto parkingRequestUpdateDto) {
        return parkingRequestRepository.queryParkingRequestById(parkingRequestUpdateDto.getId());
    }

    public Boolean existsByParkingQuotaId(Integer id){
        return parkingQuotaRepository.existsById(id);
    }

    public Boolean existsByWeekStartDate(LocalDateTime inputDateTime){
        return parkingQuotaRepository.existsByWeekStartDate(inputDateTime);
    }

    public Boolean existsByWeekStartDateExceptSelf(Integer id, LocalDateTime inputDateTime){
        return parkingQuotaRepository.existsByWeekStartDateExceptSelf(id, inputDateTime) == 1;
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Boolean findParkingRequestCheckQuota(ParkingRequestCreateDto parkingRequestCreateDto) {
        if(ObjectUtils.isEmpty(parkingRequestRepository)) {
            return false;
        }

        List<ParkingRequestEntity> currentRequest = parkingRequestRepository.getCurrentRequest(parkingRequestCreateDto.getNextWeekStartDate());
        List<ParkingQuotaEntity> parkingQuotaEntities = parkingQuotaRepository.getTotalSlots(parkingRequestCreateDto.getNextWeekStartDate());
        Integer totalSlots = ObjectUtils.isEmpty(parkingQuotaEntities) ? Integer.valueOf(0) : parkingQuotaEntities.get(0).getTotalSlots();

        return currentRequest.size() < totalSlots;
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

    public ParkingQuotaEntity updateParkingQuota(ParkingQuotaUpdateDto parkingQuotaUpdateDto){
        ParkingQuotaEntity parkingQuota = new ParkingQuotaEntity();

        parkingQuota.setId(parkingQuotaUpdateDto.getId());
        parkingQuota.setTotalSlots(parkingQuotaUpdateDto.getTotalSlots());

        return parkingQuotaRepository.save(parkingQuota);
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

    @Transactional(readOnly = true)
    public ReUserParkingRequestDto findUserParkingRequest(QueryUserParkingRequestDto queryUserParkingRequestDto, UsersBaseDto usersBaseDto) {
        List<Object[]> parkingRequestAndUsers = parkingRequestRepository.queryUserParkingRequest(
                queryUserParkingRequestDto.getWeekStartDate(),
                usersBaseDto.getUserId()
        );

        List<ReUserParkingRequestDto.parkingRequest> parkingRequestList = parkingRequestAndUsers.stream().map(dto -> {
            ReUserParkingRequestDto.parkingRequest parkingRequest = new ReUserParkingRequestDto.parkingRequest();

            parkingRequest.setRequestTime(dto[0] instanceof Timestamp ? ((Timestamp) dto[0]).toLocalDateTime() : null);
            parkingRequest.setChineseName(dto[1] != null ? dto[1].toString() : "");
            parkingRequest.setCarType(dto[2] != null ? dto[2].toString() : "");
            parkingRequest.setCarNumber(maskCarNumber(dto[3] != null ? dto[3].toString() : ""));
            parkingRequest.setCellphone(maskCellphone(dto[4] != null ? dto[4].toString() : ""));
            parkingRequest.setParkingSlotNumber(dto[5] != null ? Integer.valueOf(dto[5].toString()) : null);
            parkingRequest.setStatus(dto[6] != null ? ParkingRequestEnum.getNameByCode(dto[6].toString()) : "");

            return parkingRequest;
        }).toList();

        return new ReUserParkingRequestDto(parkingRequestList);
    }

    @Transactional(readOnly = true)
    public List<ReParkingRequestDto.parkingRequest> findParkingRequest(QueryParkingRequestDto queryParkingRequestDto) {
        List<Object[]> parkingRequestAndUsers = parkingRequestRepository.queryParkingRequestByFmId(queryParkingRequestDto.getWeekStartDate());

        if(parkingRequestAndUsers.size() <= 0) {
            return new ArrayList<>();
        }

        List<ReParkingRequestDto.parkingRequest> parkingRequestList = parkingRequestAndUsers.stream().map(dto -> {
            ReParkingRequestDto.parkingRequest parkingRequest = new ReParkingRequestDto.parkingRequest();

            parkingRequest.setRequestTime(dto[0] instanceof Timestamp ? ((Timestamp) dto[0]).toLocalDateTime() : null);
            parkingRequest.setChineseName(dto[1] != null ? dto[1].toString() : "");
            parkingRequest.setCarType(dto[2] != null ? dto[2].toString() : "");
            parkingRequest.setCarNumber(dto[3] != null ? dto[3].toString() : "");
            parkingRequest.setCellphone(dto[4] != null ? dto[4].toString() : "");
            parkingRequest.setParkingSlotNumber(dto[5] != null ? Integer.valueOf(dto[5].toString()) : null);
            parkingRequest.setStatus(dto[6] != null ? ParkingRequestEnum.getNameByCode(dto[6].toString()) : "");
            parkingRequest.setRequestId(Integer.valueOf(dto[7].toString()));

            return parkingRequest;
        }).toList();

        return parkingRequestList;
    }

    @Transactional(readOnly = true)
    public ParkingQuotaEntity findParkingQuotaByWeekStartDate(LocalDateTime weekStartDate) {
        ParkingQuotaEntity entities = parkingQuotaRepository.findByWeekStartDate(weekStartDate);

        return entities;
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

        String firstChar = carNumber.substring(0, 1); // 第一個字母
        String lastChar = carNumber.substring(carNumber.length() - 1); // 最後一碼
        String maskedMiddle = "*".repeat(dashIndex - 1) + "-" + "*".repeat(carNumber.length() - dashIndex - 2);

        return firstChar + maskedMiddle + lastChar;
    }

    /**
     * 手機號碼打碼，僅保留前三碼與最後一碼，其餘以 '*' 取代
     */
    private String maskCellphone(String cellphone) {
        return cellphone.substring(0, 3) + "*".repeat(cellphone.length() - 6) + cellphone.substring(cellphone.length() - 3);
    }
}
