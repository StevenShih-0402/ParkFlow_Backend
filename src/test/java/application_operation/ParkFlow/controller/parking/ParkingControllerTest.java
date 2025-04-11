package application_operation.ParkFlow.controller.parking;

import application_operation.ParkFlow.controller.parking.payload.ParkingQuotaCreateRq;
import application_operation.ParkFlow.controller.parking.payload.ParkingQuotaUpdateRq;
import application_operation.ParkFlow.dto.parking.create.ParkingQuotaDto;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exceptionHandler.GlobalExceptionHandler;
import application_operation.ParkFlow.service.parking.ParkingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ParkingControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper; // 將物件轉換成 Json 字串

    @Mock
    private ParkingService parkingService;

    @InjectMocks
    private ParkingController parkingController;

    private static final String createParkingQuotaPath = "/v1/parking/create-parking-quota";
    private static final String updateParkingQuotaPath = "/v1/parking/update-parking-quota";


    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Jackson 不支援 LocalDateTime 類別轉換成 JSON，需另外注入支援 Java 8 時間類型的模組 (jackson-datatype-jsr310)

        mockMvc = MockMvcBuilders
                .standaloneSetup(parkingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("ParkingController.createParkingQuota()_success")
    public void createParkingQuota_success() throws Exception{

        // 模擬回傳的 DTO
        ParkingQuotaDto parkingQuotaDto = new ParkingQuotaDto();
        parkingQuotaDto.setId(1);
        parkingQuotaDto.setTotalSlots(15);

        // 輸入的資料
        ParkingQuotaCreateRq parkingQuotaCreateRq = new ParkingQuotaCreateRq();
        parkingQuotaCreateRq.setStartDate(LocalDateTime.parse("2025-04-14T00:00:00"));
        parkingQuotaCreateRq.setTotalSlots(15);

        when(parkingService.createParkingQuota(any(ParkingQuotaCreateRq.class))).thenReturn(parkingQuotaDto);

        // 確認 Data 是否有內容，並回傳 0000 Success
        mockMvc.perform(post(createParkingQuotaPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(parkingQuotaCreateRq))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.SUCCESS.getResponseCode()))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("ParkingController.createParkingQuota()_failed")
    public void createParkingQuota_failed() throws Exception{

        // 錯誤情境1: 日期格式錯誤
        // Spring 的 Jackson 套件會在 Controller 層將 JSON 字串轉成 Rq 物件，所以測試時可以直接輸入字串，還可以避開 Java 物件的編譯錯誤
        String dateErrorRq = """
                {
                    "startDate": "20250414",
                    "totalSlots": 10
                }
                """;

        // 日期格式錯誤，回傳 9000
        mockMvc.perform(post(createParkingQuotaPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(dateErrorRq))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.INPUT_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.data").doesNotExist());

        // 錯誤情境2: 車位數量超過 Integer 範圍
        String totalSlotsExceedRq = """
                {
                    "startDate": "2025-04-14T00:00:00",
                    "totalSlots": 2147483648
                }
                """;

        // 車位數量超過 Integer 範圍，回傳 9000
        mockMvc.perform(post(createParkingQuotaPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(totalSlotsExceedRq))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.INPUT_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("ParkingController.updateParkingQuota()_success")
    public void updateParkingQuota_success() throws Exception{

        // 模擬回傳的 DTO
        ParkingQuotaDto parkingQuotaDto = new ParkingQuotaDto();
        parkingQuotaDto.setId(1);
        parkingQuotaDto.setTotalSlots(30);

        // 輸入的資料
        ParkingQuotaUpdateRq parkingQuotaUpdateRq = new ParkingQuotaUpdateRq();
        parkingQuotaUpdateRq.setId(1);
        parkingQuotaUpdateRq.setTotalSlots(30);

        when(parkingService.updateParkingQuota(any(ParkingQuotaUpdateRq.class))).thenReturn(parkingQuotaDto);

        // 確認 Data 是否有內容，並回傳 0000 Success
        mockMvc.perform(put(updateParkingQuotaPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(parkingQuotaUpdateRq))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.SUCCESS.getResponseCode()))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("ParkingController.updateParkingQuota()_failed")
    public void updateParkingQuota_failed() throws Exception{

        // 錯誤情境1: id 沒有讀取到
        ParkingQuotaUpdateRq idNullRq = new ParkingQuotaUpdateRq();
        idNullRq.setId(null);
        idNullRq.setTotalSlots(30);

        // id 沒有讀取到，回傳 9000
        mockMvc.perform(post(createParkingQuotaPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(idNullRq))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.INPUT_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.data").doesNotExist());

        // 錯誤情境2: 車位數量超過 Integer 範圍
        String totalSlotsExceedRq = """
                {
                    "id": 1,
                    "totalSlots": 2147483648
                }
                """;

        // 車位數量超過 Integer 範圍，回傳 9000
        mockMvc.perform(post(createParkingQuotaPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(totalSlotsExceedRq))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.INPUT_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
