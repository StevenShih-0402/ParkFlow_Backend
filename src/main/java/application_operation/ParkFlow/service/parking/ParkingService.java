package application_operation.ParkFlow.service.parking;

import application_operation.ParkFlow.Response.SuccessResponse;
import application_operation.ParkFlow.config.EmailConfig;
import application_operation.ParkFlow.controller.parking.payload.*;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.mail.EmailDto;
import application_operation.ParkFlow.dto.mail.SendEmailDto;
import application_operation.ParkFlow.dto.parking.create.ParkingQuotaCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestCreateDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryParkingRequest.QueryParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.QueryUserAndRoleDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.QueryUserParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryParkingRequest.ReParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.ReUserParkingRequestDto;
import application_operation.ParkFlow.dto.parking.update.ParkingQuotaUpdateDto;
import application_operation.ParkFlow.dto.parking.update.ParkingRequestUpdateDto;
import application_operation.ParkFlow.dto.parking.update.UpdateParkingRequestDto;
import application_operation.ParkFlow.entity.ParkingQuotaEntity;
import application_operation.ParkFlow.entity.ParkingRequestEntity;
import application_operation.ParkFlow.enums.ParkingRequestEnum;
import application_operation.ParkFlow.enums.RoleNameEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import application_operation.ParkFlow.service.ValidUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import application_operation.ParkFlow.dao.parking.ParkingDao;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ParkingService {

    @Value("${send.mail.to.application}")
    private boolean isSendApplicationMail;

    @Value("${send.email.to.request}")
    private boolean isSendRequestMail;

    private final ParkingDao parkingDao;
    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    private final ValidUtils validUtils;
    private final EmailConfig emailConfig;

    public boolean isValidRequest(LocalDateTime now, LocalDateTime requestedDate, LocalDateTime nextWeekStartDate) {

        // 計算下下週開始時間
        LocalDateTime nextNextWeekStartDate = nextWeekStartDate.plusWeeks(1);

        // 取得 "本週四 00:00"
        LocalDateTime thisThursday = now.toLocalDate()
                .with(DayOfWeek.THURSDAY)
                .atStartOfDay();

        // **條件 1：申請時間屬於「下週」範圍**
        boolean isNextWeek = !requestedDate.isBefore(nextWeekStartDate) && requestedDate.isBefore(nextNextWeekStartDate);

        // **條件 2：現在時間必須在「本週四之前」才能申請「下週」**
        boolean isBeforeThursday = now.isBefore(thisThursday);

        // **如果申請的是「下週」，必須在「本週四前」申請**
        if (isNextWeek) {
            return isBeforeThursday;
        }

        // **如果申請的是「下下週及以後」，則隨時可以申請**
        return true;
    }

    public ParkingRequestDto create (ParkinRequestCreateRq parkinRequestCreateRq) {

        // Jwt Token 驗證
        jwtUtil.validateToken();

        LocalDateTime requestedDate = parkinRequestCreateRq.getNextWeekStartDate();
        LocalDateTime now = LocalDateTime.now(); // 取得當前時間
        LocalDateTime nextWeekStartDate = LocalDateTime.now()
                .with(DayOfWeek.SUNDAY) // 設定為這周日
                .toLocalDate()
                .atStartOfDay(); // 計算下週開始時間

        if(!isValidRequest(now, requestedDate, nextWeekStartDate)) {
            throw new HandleException("Exceeded application time.");
        }

        // 檢查手機號碼要10碼
        if(!parkinRequestCreateRq.getCellPhone().matches("^\\d{10}$")){
            throw new HandleException("Invalid format: The phone number must be 10 digits.");
        }

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        // Rq -> Dto
        ParkingRequestCreateDto parkingRequestCreateDto = ParkingRequestCreateDto.builder()
                .nextWeekStartDate(parkinRequestCreateRq.getNextWeekStartDate())
                .cellPhone(parkinRequestCreateRq.getCellPhone())
                .carNumber(parkinRequestCreateRq.getCarNumber())
                .carType(parkinRequestCreateRq.getCarType())
                .build();

        //檢查相同使用者是否重複申請
        if(!parkingDao.findParkingRequestByApplicantId(parkingRequestCreateDto, usersBaseDto)) {
            throw new HandleException("Duplicate Application.");
        }

        //檢查申請是否到達上限
        if(!parkingDao.findParkingRequestCheckQuota(parkingRequestCreateDto)) {
            throw new HandleException("Application limit reached.");
        }

        //寫入申請表
        ParkingRequestDto parkingRequestDto = parkingDao.saveParkingRequest(parkingRequestCreateDto, usersBaseDto);

        //send email
        if(isSendRequestMail) {
            SendEmailDto sendEmailDto = userDao.queryFMEmailData();

            EmailDto emailDto = new EmailDto();
            emailDto.setEmail(sendEmailDto.getEmail());
            emailDto.setSubject(String.format("[申請] 停車位使用申請 - %s", sendEmailDto.getName()));
            emailDto.setText(String.format("""
                    Dear FM
                
                    申請資訊
                    申請日期 : %s
                    車牌號碼 : %s
                    車輛類型 : %s
                    申請人員 : %s
                    聯絡電話 : %s
                
                    此信件為系統自動發送，如有任何問題，請聯繫申請人。
                    """, parkingRequestDto.getApplicationTime(),
                    parkingRequestDto.getCarNumber(),
                    parkingRequestDto.getCarType(),
                    usersBaseDto.getEnglishName(),
                    parkingRequestDto.getCellPhone()));


            emailConfig.consumeEmail(emailDto);
        }

        return parkingRequestDto;
    }

    public UpdateParkingRequestDto update(ParkingRequestUpdateRq parkingRequestUpdateRq) {

        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        // 檢查權限為 FM
        if(!usersBaseDto.getRoleName().equals(RoleNameEnum.FM.name())) {
            throw new HandleException("Permission verification error.");
        }

        // Rq -> Dto
        ParkingRequestUpdateDto parkingRequestUpdateDto = ParkingRequestUpdateDto
                .builder()
                .id(parkingRequestUpdateRq.getId())
                .status(parkingRequestUpdateRq.getStatus())
                .parkingSlotNumber(parkingRequestUpdateRq.getParkingSlotNumber())
                .build();

        // 取得申請資料
        List<ParkingRequestEntity> parkingRequestEntities = parkingDao.findParkingRequestById(parkingRequestUpdateDto);

        // 更新資料
        UpdateParkingRequestDto updateParkingRequestDto = parkingDao.updateParkingRequest(parkingRequestEntities.get(0), parkingRequestUpdateDto, usersBaseDto);

        // send email
        if(isSendApplicationMail) {
            UsersBaseDto usersBaseDto1 = new UsersBaseDto();
            usersBaseDto1.setUserId(parkingRequestEntities.get(0).getApplicantId());
            QueryUserAndRoleDto queryUserAndRoleDto = userDao.findUsersAndRoleById(usersBaseDto1);

            EmailDto emailDto = new EmailDto();
            emailDto.setEmail(queryUserAndRoleDto.getEmail());
            emailDto.setSubject(String.format("[申請] 停車位申請結果 - %s", queryUserAndRoleDto.getEnglishName()));
            emailDto.setText(String.format("""
                    Dear %s
                
                    申請資訊
                    申請日期 : %s
                    申請人員 : %s
                    車型 : %s
                    車牌號碼 : %s
                    聯絡電話 : %s
                    車位號碼 : %s
                
                    此信件為系統自動發送，如有任何問題，請聯繫 FM 諮詢。
                    """,
                    queryUserAndRoleDto.getEnglishName(),
                    parkingRequestEntities.get(0).getApplicationTime(),
                    parkingRequestEntities.get(0).getCarNumber(),
                    parkingRequestEntities.get(0).getCarType(),
                    queryUserAndRoleDto.getEnglishName(),
                    parkingRequestEntities.get(0).getCellPhone(),
                    parkingRequestUpdateRq.getParkingSlotNumber()));


            emailConfig.consumeEmail(emailDto);
        }

        return updateParkingRequestDto;
    }

    public ReUserParkingRequestDto queryUserParkingRequest(QueryUserParkingRequestRq queryUserParkingRequestRq) {

        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        // 驗證是不是一般使用者
        if(!userDao.findUsersAndRoleById(usersBaseDto).getRoleName().equals(RoleNameEnum.USER.name())) {
            throw new HandleException("Permission Denied.");
        }

        // Rq -> Dto
        QueryUserParkingRequestDto queryUserParkingRequestDto = QueryUserParkingRequestDto.builder()
                .weekStartDate(queryUserParkingRequestRq.getWeekStartDate())
                .build();

        // 搜尋結果
        return parkingDao.findUserParkingRequest(queryUserParkingRequestDto, usersBaseDto);
    }

    public ReParkingRequestDto queryFmParkingRequest(QueryParkingRequestRq queryParkingRequestRq) {

        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        // 驗證是不是FM
        if(!userDao.findUsersAndRoleById(usersBaseDto).getRoleName().equals(RoleNameEnum.FM.name())) {
            throw new HandleException("Permission Denied.");
        }

        // Rq -> Dto
        QueryParkingRequestDto queryParkingRequestDto = QueryParkingRequestDto.builder()
                .weekStartDate(queryParkingRequestRq.getWeekStartDate())
                .build();

        List<ReParkingRequestDto.parkingRequest> parkingRequest = parkingDao.findParkingRequest(queryParkingRequestDto);
        Integer totalSlots = parkingDao.findParkingQuotaByWeekStartDate(queryParkingRequestDto.getWeekStartDate());
        int parkingRequestCount = (int) parkingRequest.stream()
                .filter(x -> x.getStatus().equals(ParkingRequestEnum.APPROVED.name()) || x.getStatus().equals(ParkingRequestEnum.REVIEW.name()))
                .count();
        Integer remainingQuantity = totalSlots - parkingRequestCount;

        // 搜尋結果
        ReParkingRequestDto reParkingRequestDto = new ReParkingRequestDto();
        reParkingRequestDto.setParkingRequestList(parkingRequest);
        reParkingRequestDto.setTotalSlots(totalSlots);
        reParkingRequestDto.setRemainingQuantity(remainingQuantity);

        return reParkingRequestDto;
    }

    public Integer createParkingQuota(ParkingQuotaCreateRq parkingQuotaCreateRq){

        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        // 檢查權限為 FM
        if(!usersBaseDto.getRoleName().equals(RoleNameEnum.FM.name())) {
            throw new HandleException("Permission verification error.");
        }

        ParkingQuotaCreateDto parkingQuotaCreateDto = new ParkingQuotaCreateDto();
        BeanUtils.copyProperties(parkingQuotaCreateRq, parkingQuotaCreateDto);

        validUtils.validateAfterToday(parkingQuotaCreateDto.getWeekStartDate());
        validUtils.validateDateNotRepeat(parkingQuotaCreateDto.getWeekStartDate());

        ParkingQuotaEntity saveEntity = parkingDao.saveParkingQuota(parkingQuotaCreateDto);
        return saveEntity.getTotalSlots();
    }

    public Integer updateParkingQuota(ParkingQuotaUpdateRq parkingQuotaUpdateRq){
        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 取得使用者個人資料
        UsersBaseDto usersBaseDto = jwtUtil.getUserBase();

        // 檢查權限為 FM
        if(!usersBaseDto.getRoleName().equals(RoleNameEnum.FM.name())) {
            throw new HandleException("Permission verification error.");
        }

        ParkingQuotaUpdateDto parkingQuotaUpdateDto = new ParkingQuotaUpdateDto();
        BeanUtils.copyProperties(parkingQuotaUpdateRq, parkingQuotaUpdateDto);

        validUtils.validateNotExistsByParkingQuotaId(parkingQuotaUpdateDto.getId());
        validUtils.validateDateNotRepeatExceptSelf(parkingQuotaUpdateDto.getId(), parkingQuotaUpdateDto.getWeekStartDate());

        ParkingQuotaEntity updateEntity = parkingDao.updateParkingQuota(parkingQuotaUpdateDto);
        return updateEntity.getTotalSlots();
    }
}
