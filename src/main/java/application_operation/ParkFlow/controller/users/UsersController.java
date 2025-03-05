package application_operation.ParkFlow.controller.users;

import application_operation.ParkFlow.dto.UserCreateDto;
import application_operation.ParkFlow.service.users.UsersService;
import io.swagger.v3.oas.annotations.Operation;
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
    ResponseEntity<UserCreateDto> create(@RequestBody UserCreateDto userCreateDto){
        return ResponseEntity.ok(usersService.create(userCreateDto));
    }
}
