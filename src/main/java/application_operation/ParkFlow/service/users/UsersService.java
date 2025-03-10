package application_operation.ParkFlow.service.users;

import application_operation.ParkFlow.Response.SuccessResponse;
import application_operation.ParkFlow.controller.users.payload.UserCreateRq;
import application_operation.ParkFlow.controller.users.payload.UserLoginRq;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.dto.users.create.UserCreateDto;
import application_operation.ParkFlow.dto.users.create.UserLoginDto;
import application_operation.ParkFlow.entity.UserEntity;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.enums.RoleNameEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import application_operation.ParkFlow.service.ValidUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserDao userDao;
    private final JwtUtil jwtUtils;
    private final ValidUtils validUtils;

    public SuccessResponse<String> create(UserCreateRq userCreateRq){

        UserCreateDto userCreateDto = new UserCreateDto();
        BeanUtils.copyProperties(userCreateRq, userCreateDto);

        validUtils.validateChineseName(userCreateDto.getChineseName());  // ChineseName 驗證
        validUtils.validateEnglishName(userCreateDto.getEnglishName());  // EnglishName 驗證
        validUtils.validateEmail(userCreateDto.getEmail());  // Email 重複註冊驗證
        validUtils.validateCellphone(userCreateDto.getCellphone());  // 電話號碼格式驗證

        UserEntity savedUser = userDao.saveUser(userCreateDto);

        String token = jwtUtils.generateToken(
            savedUser.getId(),
            savedUser.getChineseName(),
            savedUser.getEnglishName(),
            savedUser.getEmail(),
            savedUser.getCellphone(),
            savedUser.getCarNumber(),
            savedUser.getCarType(),
            RoleNameEnum.getRoleNameById(savedUser.getRoleId())
        );

        return SuccessResponse.<String>builder()
                .data(token)
                .build();
    }



    public SuccessResponse<String> login(UserLoginRq userLoginRq){

        UserLoginDto userLoginDto = new UserLoginDto();
        BeanUtils.copyProperties(userLoginRq, userLoginDto);

        // Email 不存在的話，回傳 Success 0001
        if(!userDao.existEmail(userLoginDto.getEmail())){
            return SuccessResponse.<String>builder()
                    .code(ResponseCodeEnum.REGISTER_REQ.getResponseCode())// 0001
                    .data("找不到對應的使用者，請註冊新用戶。")
                    .build();
        }

        // 存在的話，用 Email 找出用戶資料並包裝成 Jwt
        UserEntity userData = userDao.queryUserByEmail(userLoginDto);

        String token = jwtUtils.generateToken(
                userData.getId(),
                userData.getChineseName(),
                userData.getEnglishName(),
                userData.getEmail(),
                userData.getCellphone(),
                userData.getCarNumber(),
                userData.getCarType(),
                RoleNameEnum.getRoleNameById(userData.getRoleId())
        );

        return SuccessResponse.<String>builder()
                .data(token)
                .build();
    }
}
