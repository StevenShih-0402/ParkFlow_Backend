package application_operation.ParkFlow.service.parking;

import application_operation.ParkFlow.controller.parking.payload.ParkingRequestCreateRq;
import application_operation.ParkFlow.dao.parking.ParkingDao;
import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import application_operation.ParkFlow.service.ValidUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    @InjectMocks
    private ParkingService parkingService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ValidUtils validUtils;

    @Mock
    private ParkingDao parkingDao;

    @Test
    @DisplayName("ParkingService.create()_success")
    public void create_success() throws Exception {
        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //申請日期檢查
        when(validUtils.isValidRequest(any(), any(), any())).thenReturn(true);
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .email("min@gmail.com")
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
    public void create_failed() throws Exception {
        // Jwt Token 驗證
        doNothing().when(jwtUtil).validateToken();
        //申請日期檢查
        when(validUtils.isValidRequest(any(), any(), any())).thenReturn(true);
        //取得使用者資訊
        when(jwtUtil.getUserBase()).thenReturn(UsersBaseDto.builder()
                .userId(1)
                .email("min@gmail.com")
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
        Assertions.assertEquals("業務邏輯錯誤：本週尚未設定停車上限，無法申請。", exception.getMessage());
    }
}
