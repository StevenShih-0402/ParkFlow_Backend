package application_operation.ParkFlow.service;

import application_operation.ParkFlow.dao.parking.ParkingDao;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.exception.HandleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ValidUtils {

    private final UserDao userDao;

    // 身分驗證 - User
    public void isUser(String roleName){
        if(!roleName.equals(userDao.findRoleName(1))) {
            throw new HandleException("權限驗證錯誤。");
        }
    }

    // 身分驗證 - FM
    public void isFM(String roleName){
        if(!roleName.equals(userDao.findRoleName(2))) {
            throw new HandleException("權限驗證錯誤。");
        }
    }

    public Boolean isValidRequest(LocalDateTime now, LocalDateTime requestedDate, LocalDateTime nextWeekStartDate) {

        // 計算下下週開始時間
        LocalDateTime nextNextWeekStartDate = nextWeekStartDate.plusWeeks(1);

        // 取得 "本週四 00:00"
        LocalDateTime thisThursday = now.toLocalDate()
                .with(DayOfWeek.THURSDAY)
                .atStartOfDay();

        // **條件 1：申請時間屬於「下週」範圍**
        Boolean isNextWeek = !requestedDate.isBefore(nextWeekStartDate) && requestedDate.isBefore(nextNextWeekStartDate);

        // **條件 2：現在時間必須在「本週四之前」才能申請「下週」**
        Boolean isBeforeThursday = now.isBefore(thisThursday);

        // **如果申請的是「下週」，必須在「本週四前」申請**
        if (isNextWeek) {
            return isBeforeThursday;
        }

        // **如果申請的是「下下週及以後」，則隨時可以申請**
        return true;
    }
}