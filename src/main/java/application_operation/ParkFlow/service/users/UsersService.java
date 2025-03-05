package application_operation.ParkFlow.service.users;

import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.dto.UserCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserDao userDao;

    public UserCreateDto create(UserCreateDto userCreateDto){

        // ChineseName 驗證
        if(!userCreateDto.getChineseName().matches("^[\\p{IsHan}]+$")){
            throw new IllegalArgumentException("只能輸入中文。");
        }

        // EnglishName 驗證
        if(!userCreateDto.getEnglishName().matches("^[A-Za-z\\- ]+$")){
            throw new IllegalArgumentException("只能輸入英文與連接線。");
        }

        // Email 格式驗證
        if(!userCreateDto.getEmail().matches("^[^@]+@[^@]+$")){
            throw new IllegalArgumentException("格式錯誤，@ 的左邊與右邊必須有值。");
        }

        // Email 重複驗證
        if(userDao.existEmail(userCreateDto.getEmail())){
            throw new IllegalArgumentException("已經有重複的 Email。");
        }

        // 電話號碼格式驗證
        if(!userCreateDto.getCellphone().matches("^\\d{10}$")){
            throw new IllegalArgumentException("格式錯誤，電話號碼必須為10個數字。");
        }

        // 電話號碼重複驗證
        if(userDao.existCellphone(userCreateDto.getCellphone())){
            throw new IllegalArgumentException("已經有重複的電話號碼。");
        }

        return userDao.saveUser(userCreateDto);
    }
}
