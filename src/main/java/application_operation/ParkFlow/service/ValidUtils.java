package application_operation.ParkFlow.service;

import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.exception.HandleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidUtils {

    private final UserDao userDao;

    // 中文姓名
    public void validateChineseName(String chineseName) {

        // 必須是中文字
        if(!chineseName.matches("^\\p{IsHan}+$")){
            throw new HandleException("只能輸入中文。");
        }
    }

    // 英文姓名
    public void validateEnglishName(String englishName) {

        // 必須是英文字
        if(!englishName.matches("^[A-Za-z\\- ]+$")){
            throw new HandleException("只能輸入英文、空格與連接線。");
        }
    }

    // 信箱
    public void validateEmail(String email) {

        // 信箱是否已經註冊過
        if(userDao.existEmail(email)){
            throw new HandleException("此信箱已經被註冊過。");
        }
    }

    // 行動電話
    public void validateCellphone(String cellphone) {

        // 欄位長度
        if(!cellphone.matches("^09\\d{8}$")){
            throw new HandleException("行動電話必須為台灣電話號碼格式 (09開頭，共10個數字)。");
        }
    }
}
