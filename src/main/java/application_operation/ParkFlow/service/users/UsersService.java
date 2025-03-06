package application_operation.ParkFlow.service.users;

import application_operation.ParkFlow.controller.users.payload.UserCreateRq;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.dto.user.create.UserCreateDto;
import application_operation.ParkFlow.entity.UserEntity;
import application_operation.ParkFlow.enums.RoleNameEnum;
import application_operation.ParkFlow.exception.HandleException;
import application_operation.ParkFlow.jwtToken.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserDao userDao;
    private final JwtUtil jwtUtils;

    public String create(UserCreateRq userCreateRq){

        UserCreateDto userCreateDto = new UserCreateDto();
        BeanUtils.copyProperties(userCreateRq, userCreateDto);

        // ChineseName 驗證
        if(!userCreateDto.getChineseName().matches("^[\\p{IsHan}]+$")){
            throw new HandleException("只能輸入中文。");
        }

        // EnglishName 驗證
        if(!userCreateDto.getEnglishName().matches("^[A-Za-z\\- ]+$")){
            throw new HandleException("只能輸入英文與連接線。");
        }

        // Email 重複驗證
        if(userDao.existEmail(userCreateDto.getEmail())){
            throw new HandleException("已經有重複的 Email。");
        }

        // 電話號碼格式驗證
        if(!userCreateDto.getCellphone().matches("^\\d{10}$")){
            throw new HandleException("格式錯誤，電話號碼必須為10個數字。");
        }

        UserEntity savedUser = userDao.saveUser(userCreateDto);

        return jwtUtils.generateToken(
            savedUser.getId(),
            savedUser.getChineseName(),
            savedUser.getEnglishName(),
            savedUser.getEmail(),
            savedUser.getCellphone(),
            savedUser.getCarNumber(),
            savedUser.getCarType(),
            RoleNameEnum.USER
        );
    }
}
