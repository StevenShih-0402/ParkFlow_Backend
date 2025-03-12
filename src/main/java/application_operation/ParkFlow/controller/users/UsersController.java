package application_operation.ParkFlow.controller.users;

import application_operation.ParkFlow.Response.SuccessResponse;
import application_operation.ParkFlow.controller.users.payload.UserCreateRq;
import application_operation.ParkFlow.controller.users.payload.UserLoginRq;
import application_operation.ParkFlow.controller.users.payload.UserLogoutRq;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.service.users.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/create")
    @Operation(summary = "新增使用者", description = "新增使用者")
    public ResponseEntity<SuccessResponse<String>> create(@Valid @RequestBody UserCreateRq userCreateRq){
        String token = usersService.create(userCreateRq);   // Jwt Token

        return ResponseEntity.ok(SuccessResponse.<String>builder()
                .data(token)
                .build());
    }

    @PostMapping("/login")
    @Operation(summary = "驗證使用者", description = "驗證使用者")
    public ResponseEntity<SuccessResponse<String>> login(@Valid @RequestBody UserLoginRq userLoginRq){
        String response = usersService.login(userLoginRq);

        if(response.equals(ResponseCodeEnum.REGISTER_REQ.getResponseCode())){
            return ResponseEntity.ok(SuccessResponse.<String>builder()
                    .code(response)  // 0001 跳轉至註冊介面
                    .message("找不到對應的用戶資料，請先註冊後再登入系統。")
                    .data(null)
                    .build());
        }
        return ResponseEntity.ok(SuccessResponse.<String>builder()
                .data(response)  // 登入成功回傳 Token
                .build());
    }

    @PostMapping("/logout")
    @Operation(summary = "使用者登出", description = "使用者登出")
    public ResponseEntity<SuccessResponse<String>> logout(){
        usersService.logout();

        return ResponseEntity.ok(SuccessResponse.<String>builder()
                .data(null)  // 登出不回傳任何內容
                .build());
    }
}
