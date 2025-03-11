package application_operation.ParkFlow.service;

import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.exception.HandleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ValidUtils {

    private final UserDao userDao;

    // 信箱
    public void validateEmailHasRegistered(String email) {
        // 信箱是否已經註冊過
        if(userDao.existEmail(email)){
            throw new HandleException("此信箱已經被註冊過。");
        }
    }

    // 日期
    public void validateAfterToday(LocalDate inputDate){

        LocalDate today = LocalDate.now();
        // 日期是否在今天以後
        if(inputDate.isBefore(today)){
            throw new HandleException("下週開始日期必須是在今天之後的日期。");
        }
    }
}
