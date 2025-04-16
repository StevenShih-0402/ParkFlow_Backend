package application_operation.ParkFlow.service.parking;

import application_operation.ParkFlow.controller.parking.payload.ParkingRequestCreateRq;
import application_operation.ParkFlow.controller.parking.payload.ParkingRequestUpdateRq;
import application_operation.ParkFlow.controller.parking.payload.QueryUserParkingRequestRq;
import application_operation.ParkFlow.dao.parking.ParkingDao;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.QueryUserAndRoleDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.QueryUserParkingRequestDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.ReUserParkingRequestDto;
import application_operation.ParkFlow.dto.parking.update.UpdateParkingRequestDto;
import application_operation.ParkFlow.entity.ParkingRequestEntity;
import application_operation.ParkFlow.enums.ErrorMessageEnum;
import application_operation.ParkFlow.enums.ParkingRequestEnum;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import application_operation.ParkFlow.service.ValidUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    @InjectMocks
    private ParkingService parkingService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ValidUtils validUtils;

    @Mock
    private UserDao userDao;

    @Mock
    private ParkingDao parkingDao;

    @Test
    @DisplayName("ParkingService.create()_success")
    public void create_success() {
        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //申請日期檢查
        when(validUtils.isValidRequest(any(), any(), any())).thenReturn(true);
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .roleName("User")
                .build());
        //驗證使用者權限
        doNothing().when(validUtils).isUser(any());
        // 日期是否在今天以後
        doNothing().when(validUtils).isBeforeDate(any());
        // 日期是否有重複
        when(parkingDao.existsByStartDate(any())).thenReturn(true);
        //檢查相同使用者是否重複申請
        when(parkingDao.findParkingRequestByApplicantId(any(), any())).thenReturn(true);
        //檢查申請是否到達上限
        when(parkingDao.findParkingRequestCheckQuota(any())).thenReturn(true);

        String strToStartTime = "2025-04-06T00:00:00";
        String strToApplicationTime = "2025-04-02T14:39:43";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);
        LocalDateTime applicationTime = LocalDateTime.parse(strToApplicationTime, formatter);
        //執行寫入
        when(parkingDao.saveParkingRequest(any(), any())).thenReturn(ParkingRequestDto.builder()
                .Id(1)
                .applicationTime(applicationTime)
                .startDate(startTime)
                .cellPhone("0912345678")
                .carNumber("EAF-2200")
                .carType("TOYOTA")
                .build());

        ParkingRequestCreateRq parkingRequestCreateRq = ParkingRequestCreateRq.builder()
                .startDate(startTime)
                .cellPhone("0912345678")
                .carNumber("EAF-2200")
                .carType("TOYOTA")
                .build();

        ParkingRequestDto parkingRequestDto = parkingService.create(parkingRequestCreateRq);

        Assertions.assertNotNull(parkingRequestDto);
        Assertions.assertEquals(startTime.format(formatter), parkingRequestDto.getStartDate().format(formatter));
        Assertions.assertEquals(applicationTime.format(formatter), parkingRequestDto.getApplicationTime().format(formatter));
        Assertions.assertEquals("0912345678", parkingRequestDto.getCellPhone());
        Assertions.assertEquals("EAF-2200", parkingRequestDto.getCarNumber());
        Assertions.assertEquals("TOYOTA", parkingRequestDto.getCarType());
    }

    @Test
    @DisplayName("ParkingService.create()_failed")
    public void create_failed() {
        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //申請日期檢查
        when(validUtils.isValidRequest(any(), any(), any())).thenReturn(true);
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .roleName("User")
                .build());
        //驗證使用者權限
        doNothing().when(validUtils).isUser(any());
        // 日期是否在今天以後
        doNothing().when(validUtils).isBeforeDate(any());
        // 日期是否有重複
        when(parkingDao.existsByStartDate(any())).thenReturn(false);

        String strToStartTime = "2025-04-06T00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);

        ParkingRequestCreateRq parkingRequestCreateRq =ParkingRequestCreateRq.builder()
                .startDate(startTime)
                .cellPhone("0912345678")
                .carNumber("EAF-2200")
                .carType("TOYOTA")
                .build();

        // 驗證是否有拋出 NaviException
        HandleException exception = Assertions.assertThrows(HandleException.class, () ->
                // 執行Service測試
                parkingService.create(parkingRequestCreateRq)
        );

        // 驗證錯誤碼
        Assertions.assertEquals(ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(), exception.getCode());
        Assertions.assertEquals(ErrorMessageEnum.NOT_SET_PARKING_QUOTA.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("ParkingService.update()_success")
    public void update_success() {
        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .roleName("User")
                .build());
        //驗證使用者權限
        doNothing().when(validUtils).isFM(any());
        // 資料庫是否有對應的內容
        when(parkingDao.existsByParkingRequestId(any())).thenReturn(true);

        List<ParkingRequestEntity> mockList = new ArrayList<>();
        ParkingRequestEntity entity = new ParkingRequestEntity();
        entity.setId(1);
        entity.setCellPhone("0912345678");
        entity.setCarNumber("EAF-2200");
        entity.setCarType("TOYOTA");
        entity.setStatus(ParkingRequestEnum.APPROVED);
        mockList.add(entity);

        when(parkingDao.findParkingRequestById(any())).thenReturn(mockList);

        when(parkingDao.updateParkingRequest(any(), any(), any())).thenReturn(UpdateParkingRequestDto.builder()
                .id(1)
                .status(ParkingRequestEnum.APPROVED)
                .parkingSlotNumber(10)
                .build()
        );

        ParkingRequestUpdateRq parkingRequestUpdateRq = ParkingRequestUpdateRq.builder()
                .id(1)
                .status(ParkingRequestEnum.APPROVED)
                .parkingSlotNumber(10)
                .build();

        UpdateParkingRequestDto updateParkingRequestDto = parkingService.update(parkingRequestUpdateRq);

        Assertions.assertNotNull(updateParkingRequestDto);
        Assertions.assertEquals(1, updateParkingRequestDto.getId());
        Assertions.assertEquals(ParkingRequestEnum.APPROVED, updateParkingRequestDto.getStatus());
        Assertions.assertEquals(10, updateParkingRequestDto.getParkingSlotNumber());
    }

    @Test
    @DisplayName("ParkingService.update()_failed")
    public void update_failed() {
        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .roleName("User")
                .build());
        //驗證使用者權限
        doNothing().when(validUtils).isFM(any());
        // 資料庫是否有對應的內容
        when(parkingDao.existsByParkingRequestId(any())).thenReturn(false);

        ParkingRequestUpdateRq parkingRequestUpdateRq = ParkingRequestUpdateRq.builder()
                .id(1)
                .status(ParkingRequestEnum.APPROVED)
                .parkingSlotNumber(10)
                .build();

        // 驗證是否有拋出 NaviException
        HandleException exception = Assertions.assertThrows(HandleException.class, () ->
                // 執行Service測試
                parkingService.update(parkingRequestUpdateRq)
        );

        // 驗證錯誤碼
        Assertions.assertEquals(ResponseCodeEnum.DATABASE_ERROR.getResponseCode(), exception.getCode());
        Assertions.assertEquals(ErrorMessageEnum.NOT_FOUND_PARKING_REQ.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("ParkingService.queryUserParkingRequest()_success")
    public void queryUserParkingRequest_success() {
        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .roleName("USER")
                .build());

        //取得使用者資訊
        when(userDao.findUsersAndRoleById(any())).thenReturn(QueryUserAndRoleDto.builder()
                .email("min@gmail.com")
                .englishName("min")
                .roleName("USER").build());

        //驗證使用者權限
        doNothing().when(validUtils).isUser(any());

        String strToStartTime = "2025-04-06T00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);

        when(parkingDao.findUserParkingRequest(any(), any())).thenReturn(ReUserParkingRequestDto.builder()
                .parkingRequestList(List.of(ReUserParkingRequestDto.parkingRequest.builder()
                        .requestTime(startTime)
                        .name("小明")
                        .carType("TOYOTA")
                        .carNumber("EAF-2200")
                        .cellphone("0912345678")
                        .parkingSlotNumber(10)
                        .status(ParkingRequestEnum.APPROVED.toString())
                        .build()))
                .remainingQuantity(10)
                .build());

        QueryUserParkingRequestRq queryUserParkingRequestRq = QueryUserParkingRequestRq.builder()
                .startDate(startTime)
                .build();

        ReUserParkingRequestDto reUserParkingRequestDto = parkingService.queryUserParkingRequest(queryUserParkingRequestRq);

        Assertions.assertNotNull(reUserParkingRequestDto);
        Assertions.assertEquals(startTime, reUserParkingRequestDto.getParkingRequestList().get(0).getRequestTime());
        Assertions.assertEquals("小明", reUserParkingRequestDto.getParkingRequestList().get(0).getName());
        Assertions.assertEquals("TOYOTA", reUserParkingRequestDto.getParkingRequestList().get(0).getCarType());
        Assertions.assertEquals("EAF-2200", reUserParkingRequestDto.getParkingRequestList().get(0).getCarNumber());
        Assertions.assertEquals("0912345678", reUserParkingRequestDto.getParkingRequestList().get(0).getCellphone());
        Assertions.assertEquals(10, reUserParkingRequestDto.getParkingRequestList().get(0).getParkingSlotNumber());
        Assertions.assertEquals(ParkingRequestEnum.APPROVED.toString(), reUserParkingRequestDto.getParkingRequestList().get(0).getStatus());
        Assertions.assertEquals(10, reUserParkingRequestDto.getRemainingQuantity());
    }

    @Test
    @DisplayName("ParkingService.queryUserParkingRequest()_failed")
    public void queryUserParkingRequest_failed() {
        doNothing().when(jwtUtil).validateToken();

        when(jwtUtil.getUserBase()).thenReturn(
                UsersBaseDto.builder()
                        .userId(1)
                        .roleName("User")
                        .build()
        );

        when(userDao.findUsersAndRoleById(any())).thenReturn(QueryUserAndRoleDto.builder()
                .email("min@gmail.com")
                .englishName("min")
                .roleName("FM").build());

        doThrow(new HandleException(ResponseCodeEnum.AUTH_ERROR.getResponseCode(),
                ErrorMessageEnum.NOT_USER.getMessage())).when(validUtils).isUser(any());

        String strToStartTime = "2025-04-06T00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(strToStartTime, formatter);

        QueryUserParkingRequestRq queryUserParkingRequestRq = QueryUserParkingRequestRq.builder()
                .startDate(startTime)
                .build();

        HandleException exception = Assertions.assertThrows(HandleException.class, () ->
                parkingService.queryUserParkingRequest(queryUserParkingRequestRq)
        );

        Assertions.assertEquals(ResponseCodeEnum.AUTH_ERROR.getResponseCode(), exception.getCode());
        Assertions.assertEquals(ErrorMessageEnum.NOT_USER.getMessage(), exception.getMessage());
    }
}
