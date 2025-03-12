package application_operation.ParkFlow.service.users;

import application_operation.ParkFlow.Response.SuccessResponse;
import application_operation.ParkFlow.controller.users.payload.UserCreateRq;
import application_operation.ParkFlow.controller.users.payload.UserLoginRq;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.dto.users.UserCreateDto;
import application_operation.ParkFlow.dto.users.UserLoginDto;
import application_operation.ParkFlow.entity.UserEntity;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
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

    public String create(UserCreateRq userCreateRq){

        UserCreateDto userCreateDto = new UserCreateDto();
        BeanUtils.copyProperties(userCreateRq, userCreateDto);

        validUtils.validateEmailHasRegistered(userCreateDto.getEmail());  // Email 重複註冊驗證

        UserEntity savedUser = userDao.saveUser(userCreateDto);

        return jwtUtils.generateToken(
            savedUser.getId(),
            savedUser.getChineseName(),
            savedUser.getEnglishName(),
            savedUser.getEmail(),
            savedUser.getCellphone(),
            savedUser.getCarNumber(),
            savedUser.getCarType(),
            userDao.findRoleName(savedUser.getRoleId())
        );
    }



    public String login(UserLoginRq userLoginRq){

        UserLoginDto userLoginDto = new UserLoginDto();
        BeanUtils.copyProperties(userLoginRq, userLoginDto);

        // Email 不存在的話，回傳 Success 0001
        if(!userDao.existEmail(userLoginDto.getEmail())){
            return ResponseCodeEnum.REGISTER_REQ.getResponseCode();
        }

        // 存在的話，用 Email 找出用戶資料並包裝成 Jwt
        UserEntity userData = userDao.queryUserByEmail(userLoginDto);

        return jwtUtils.generateToken(
                userData.getId(),
                userData.getChineseName(),
                userData.getEnglishName(),
                userData.getEmail(),
                userData.getCellphone(),
                userData.getCarNumber(),
                userData.getCarType(),
                userDao.findRoleName(userData.getRoleId())
        );
    }
}
