package application_operation.ParkFlow.controller.users;

import application_operation.ParkFlow.Response.SuccessResponse;
import application_operation.ParkFlow.controller.users.payload.UserCreateRq;
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
    @Operation(summary = "註冊使用者", description = "註冊使用者")
    public ResponseEntity<SuccessResponse<Object>> create(@Valid @RequestBody UserCreateRq userCreateRq){
        String token =  usersService.create(userCreateRq);   // Jwt Token
        return ResponseEntity.ok(SuccessResponse.builder()
                .data(token)
                .build());
    }
}
