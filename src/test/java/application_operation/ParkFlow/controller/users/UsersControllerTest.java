package application_operation.ParkFlow.controller.users;

import application_operation.ParkFlow.controller.users.payload.UserCreateRq;
import application_operation.ParkFlow.controller.users.payload.UserLoginRq;
import application_operation.ParkFlow.controller.users.payload.UserUpdateRq;
import application_operation.ParkFlow.dto.users.UserQueryDto;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.JwtTokenException;
import application_operation.ParkFlow.exceptionHandler.GlobalExceptionHandler;
import application_operation.ParkFlow.service.users.UsersService;
import application_operation.ParkFlow.validTag.chinesename.ChineseName;
import application_operation.ParkFlow.validTag.englishname.EnglishName;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import io.jsonwebtoken.Jwts;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Key;
import java.util.Date;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 讓 JUnit 5 搭配 Mockito 使用時，可以自動初始化 @Mock 標註的欄位，也支援 @InjectMocks 自動注入
// 只啟動 Mockito 本身的機制，不會像 @SpringBootTest 載入所有 bean、設定檔、環境變數，因此效能較好
@ExtendWith(MockitoExtension.class)
public class UsersControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper; // 將物件轉換成 Json 字串

    @Mock
    private UsersService usersService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private UsersController usersController;

    private static final String registerPath = "/v1/users/create";
    private static final String loginPath = "/v1/users/login";
    private static final String logoutPath = "/v1/users/logout";
    private static final String queryPath = "/v1/users/query";
    private static final String updatePath = "/v1/users/update";

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 28800000; // 8 小時

    // 初始化 objectMapper 和 mockMvc，單獨測試 UserController，不啟動整個 Spring 容器，並手動引入自訂的 GlobalExceptionHandler
    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .standaloneSetup(usersController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("UsersController.create()_success")
    public void create_success() throws Exception{

        // 模擬回傳的 JWT
        String token = Jwts.builder()
                        .claim("userId", 1)
                        .claim("roleName", "USER")
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                        .signWith(secretKey)
                        .compact();

        // 輸入的資料
        UserCreateRq userCreateRq = new UserCreateRq();
        userCreateRq.setChineseName("小明");
        userCreateRq.setEnglishName("Xiao Ming");
        userCreateRq.setEmail("xm@gmail.com");
        userCreateRq.setCellphone("0912345678");
        userCreateRq.setCarNumber("ABC-1234");
        userCreateRq.setCarType("TOYOTA");

        // 模擬 Service 執行後回傳結果的流程
        when(usersService.create(any(UserCreateRq.class))).thenReturn(token);

        // 確認 Data 是否有內容，並回傳 0000 Success
        assertSuccessMessage(post(registerPath), userCreateRq);
    }


    @Test
    @DisplayName("UsersController.create()_failed")
    public void create_failed() throws Exception{

        // 輸入的資料
        // 中文姓名錯誤
        UserCreateRq chnNameErrorRq = new UserCreateRq();
        chnNameErrorRq.setChineseName("Xiao Ming");
        chnNameErrorRq.setEnglishName("Xiao Ming");
        chnNameErrorRq.setEmail("xm@gmail.com");
        chnNameErrorRq.setCellphone("0912345678");
        chnNameErrorRq.setCarNumber("ABC-1234");
        chnNameErrorRq.setCarType("TOYOTA");

        // 中文姓名錯誤，回傳 9000
        assertInputErrorMessage(
                post(registerPath),
                chnNameErrorRq,
                ChineseName.DEFAULT_MESSAGE
                );
    }


    @Test
    @DisplayName("UsersController.login()_success")
    public void login_success() throws Exception{

        // 模擬登入後回傳的 JWT
        String token = Jwts.builder()
                .claim("userId", 1)
                .claim("roleName", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();

        // 輸入的資料
        UserLoginRq userLoginRq = new UserLoginRq();
        userLoginRq.setEmail("xm@gmail.com");

        when(usersService.login(any(UserLoginRq.class))).thenReturn(token);

        // 確認 Data 是否有內容，並回傳 0000 Success
        assertSuccessMessage(post(loginPath), userLoginRq);
    }


    @Test
    @DisplayName("UsersController.login()_failed")
    public void login_failed() throws Exception{

        // 錯誤情境: 信箱格式錯誤
        UserLoginRq emailErrorRq = new UserLoginRq();
        emailErrorRq.setEmail("xm.gmail.com");

        // 信箱格式錯誤，回傳 9000
        assertInputErrorMessage(
                post(loginPath),
                emailErrorRq,
                messageSource.getMessage("email.format", null, Locale.TAIWAN)
        );
    }


    @Test
    @DisplayName("UsersController.logout()_success")
    public void logout_success() throws Exception{

        // 驗證 usersService.logout() 有被呼叫
        doNothing().when(usersService).logout();

        // 確認 Data 是否有內容，並回傳 0000 Success
        mockMvc.perform(post(logoutPath))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.SUCCESS.getResponseCode()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    @DisplayName("UsersController.logout()_failed")
    public void logout_failed() throws Exception{
        String errorMessage = "身分驗證錯誤：Token 不存在或格式錯誤。";

        // 模擬 usersService.logout() 裡發生例外
        doThrow(new JwtTokenException(errorMessage)).when(usersService).logout();

        // Token 遺失或過期，回傳 9001
        mockMvc.perform(post(logoutPath))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.AUTH_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    @DisplayName("UsersController.query()_success")
    public void query_success() throws Exception{

        // 模擬回傳的 DTO
        UserQueryDto userQueryDto = new UserQueryDto();
        userQueryDto.setId(1);
        userQueryDto.setChineseName("小明");
        userQueryDto.setEnglishName("Xiao Ming");
        userQueryDto.setEmail("xs@gmail.com");
        userQueryDto.setCellphone("0912345678");
        userQueryDto.setCarNumber("ABC-1234");
        userQueryDto.setCarType("TOYOTA");

        when(usersService.query()).thenReturn(userQueryDto);

        // 確認 Data 是否有內容，並回傳 0000 Success
        mockMvc.perform(post(queryPath))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.SUCCESS.getResponseCode()))
                .andExpect(jsonPath("$.data").exists());
    }


    @Test
    @DisplayName("UsersController.query()_failed")
    public void query_failed() throws Exception{
        String errorMessage = "身分驗證錯誤：Token 不存在或格式錯誤。";

        // 模擬 usersService.query() 裡發生例外
        doThrow(new JwtTokenException(errorMessage)).when(usersService).query();

        // Token 遺失或過期，回傳 9001
        mockMvc.perform(post(queryPath))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.AUTH_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    @DisplayName("UsersController.update()_success")
    public void update_success() throws Exception{

        // 模擬回傳修改後的 DTO
        UserQueryDto userQueryDto = new UserQueryDto();
        userQueryDto.setId(1);
        userQueryDto.setChineseName("小美");
        userQueryDto.setEnglishName("Xiao Mei");
        userQueryDto.setEmail("xm@gmail.com");
        userQueryDto.setCellphone("0987654321");
        userQueryDto.setCarNumber("CBA-4321");
        userQueryDto.setCarType("MAZDA");

        // 模擬輸入的資料
        UserUpdateRq userUpdateRq = new UserUpdateRq();
        userUpdateRq.setChineseName("小美");
        userUpdateRq.setEnglishName("Xiao Mei");
        userUpdateRq.setCellphone("0987654321");
        userUpdateRq.setCarNumber("CBA-4321");
        userUpdateRq.setCarType("MAZDA");

        when(usersService.update(any(UserUpdateRq.class))).thenReturn(userQueryDto);

        // 確認 Data 是否有內容，並回傳 0000 Success
        mockMvc.perform(put(updatePath)
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRq))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.SUCCESS.getResponseCode()))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.chineseName").value(userUpdateRq.getChineseName()))
                .andExpect(jsonPath("$.data.englishName").value(userUpdateRq.getEnglishName()))
                .andExpect(jsonPath("$.data.cellphone").value(userUpdateRq.getCellphone()))
                .andExpect(jsonPath("$.data.carNumber").value(userUpdateRq.getCarNumber()))
                .andExpect(jsonPath("$.data.carType").value(userUpdateRq.getCarType()));
    }


    @Test
    @DisplayName("UsersController.update()_failed")
    public void update_failed() throws Exception{

        // 錯誤情境: 英文姓名錯誤
        UserUpdateRq engNameErrorRq = new UserUpdateRq();
        engNameErrorRq.setChineseName("小美");
        engNameErrorRq.setEnglishName("小美");
        engNameErrorRq.setCellphone("0987654321");
        engNameErrorRq.setCarNumber("CBA-4321");
        engNameErrorRq.setCarType("MAZDA");

        // 英文姓名錯誤，回傳 9000
        assertInputErrorMessage(
                put(updatePath),
                engNameErrorRq,
                EnglishName.DEFAULT_MESSAGE
                );
    }



    // 驗證是否回傳成功訊息
    public void assertSuccessMessage(MockHttpServletRequestBuilder httpMethod, Object request) throws Exception {
        mockMvc.perform(httpMethod
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.SUCCESS.getResponseCode()))
                .andExpect(jsonPath("$.data").exists());
    }

    // 驗證 Rq 的 Annotation 是否有運作
    public void assertInputErrorMessage(MockHttpServletRequestBuilder httpMethod, Object request, String errorMessage) throws Exception {
        mockMvc.perform(httpMethod
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCodeEnum.INPUT_ERROR.getResponseCode()))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}