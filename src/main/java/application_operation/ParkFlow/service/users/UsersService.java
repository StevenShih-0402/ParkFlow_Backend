package application_operation.ParkFlow.service.users;

import application_operation.ParkFlow.controller.users.payload.UserCreateRq;
import application_operation.ParkFlow.controller.users.payload.UserLoginRq;
import application_operation.ParkFlow.controller.users.payload.UserLogoutRq;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.dto.users.UserCreateDto;
import application_operation.ParkFlow.dto.users.UserLoginDto;
import application_operation.ParkFlow.dto.users.UserLogoutDto;
import application_operation.ParkFlow.entity.UserEntity;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.JwtTokenException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import application_operation.ParkFlow.service.ValidUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    private final ValidUtils validUtils;

    public String create(UserCreateRq userCreateRq){

        UserCreateDto userCreateDto = new UserCreateDto();
        BeanUtils.copyProperties(userCreateRq, userCreateDto);

        validUtils.validateEmailHasRegistered(userCreateDto.getEmail());  // Email 重複註冊驗證

        UserEntity savedUser = userDao.saveUser(userCreateDto);

        return jwtUtil.generateToken(
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

        return jwtUtil.generateToken(
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

    public void logout(){
        // Jwt Token 驗證
        jwtUtil.validateToken();

        // 從 Authorization 標頭(authHeader) 取出 Token
        String token = jwtUtil.extractTokenFromAuthHeader();

        // 將 Token 加入黑名單
        jwtUtil.blacklistToken(token);
    }
}
