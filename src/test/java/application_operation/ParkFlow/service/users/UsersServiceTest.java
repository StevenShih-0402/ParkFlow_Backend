package application_operation.ParkFlow.service.users;

import application_operation.ParkFlow.controller.users.UsersController;
import application_operation.ParkFlow.controller.users.payload.UserCreateRq;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.dto.users.UserCreateDto;
import application_operation.ParkFlow.entity.UserEntity;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.exceptionHandler.GlobalExceptionHandler;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigInteger;
import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsersService usersService;

    @Test
    @DisplayName("UsersService.create()_success")
    public void create_success() {

        // 1. 準備階段 (Arrange)
        // 建立 Rq
        UserCreateRq userCreateRq = new UserCreateRq();
        userCreateRq.setChineseName("小明");
        userCreateRq.setEnglishName("Xiao Ming");
        userCreateRq.setEmail("xm@gmail.com");
        userCreateRq.setCellphone("0912345678");
        userCreateRq.setCarNumber("ABC-1234");
        userCreateRq.setCarType("TOYOTA");

        // 模擬 DAO 層回傳的資料，因為這邊不能真的進入 DB。
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setChineseName("小明");
        userEntity.setEnglishName("Xiao Ming");
        userEntity.setEmail("xm@gmail.com");
        userEntity.setCellphone("0912345678");
        userEntity.setCarNumber("ABC-1234");
        userEntity.setCarType("TOYOTA");
        userEntity.setRoleId(1);

        // 模擬信箱不重複的情境
        when(userDao.existEmail(any())).thenReturn(false);

        // 模擬 UserDao.saveUser() 執行後回傳結果的情境。
        when(userDao.saveUser(any())).thenReturn(userEntity);

        // 模擬用 roleId 查詢 roleName 的情境
        when(userDao.findRoleName(any())).thenReturn("ROLE_NAME");

        // 模擬生成 Jwt 的情境
        when(jwtUtil.generateToken(any(), any())).thenReturn("mock-jwt-string");


        // 2. 執行階段 (Act)
        // 執行 UsersService.create()。
        String token = usersService.create(userCreateRq);

        // 3. 驗證階段 (Assert)
        // 驗證 token 不是 Null
        assertNotNull(token);
    }

    @Test
    @DisplayName("UsersService.create()_failed")
    public void create_failed() {
        // 錯誤情境：信箱已被註冊過
        // 建立 Rq
        UserCreateRq userCreateRq = new UserCreateRq();
        userCreateRq.setChineseName("小明");
        userCreateRq.setEnglishName("Xiao Ming");
        userCreateRq.setEmail("xiaoming@gmail.com");
        userCreateRq.setCellphone("0912345678");
        userCreateRq.setCarNumber("ABC-1234");
        userCreateRq.setCarType("TOYOTA");

        // 模擬信箱重複的情境
        when(userDao.existEmail(any())).thenReturn(true);

        // 執行 usersService.create 驗證拋出的錯誤是否為 HandleException，並保存成變數 ex
        HandleException ex = assertThrows(
                HandleException.class,
                () -> usersService.create(userCreateRq)
        );

        // 驗證 ErrorMessage 的 code 是 9002
        assertEquals(ResponseCodeEnum.BUSINESS_ERROR.getResponseCode(), ex.getCode());

        // 驗證錯誤訊息的內容
        assertEquals("業務邏輯錯誤：信箱不能重複註冊。", ex.getMessage());
    }
}
