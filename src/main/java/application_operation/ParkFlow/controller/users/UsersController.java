package application_operation.ParkFlow.controller.users;

import application_operation.ParkFlow.Response.SuccessResponse;
import application_operation.ParkFlow.controller.users.payload.UserCreateRq;
import application_operation.ParkFlow.controller.users.payload.UserLoginRq;
import application_operation.ParkFlow.service.users.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "user-controller", description = "新增使用者")
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/create")
    @Operation(summary = "新增使用者", description = "新增使用者")
    public ResponseEntity<SuccessResponse<String>> create(@Valid @RequestBody UserCreateRq userCreateRq){
        return ResponseEntity.ok(usersService.create(userCreateRq));   // Jwt Token
    }

    @PostMapping("/login")
    @Operation(summary = "驗證使用者", description = "驗證使用者")
    public ResponseEntity<SuccessResponse<String>> login(@Valid @RequestBody UserLoginRq userLoginRq){
        return ResponseEntity.ok(usersService.login(userLoginRq));  // Jwt Token or 需要註冊
    }
}
