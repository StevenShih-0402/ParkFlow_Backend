package application_operation.ParkFlow.service;

import application_operation.ParkFlow.dao.parking.ParkingDao;
import application_operation.ParkFlow.dao.users.UserDao;
import application_operation.ParkFlow.enums.RoleNameEnum;
import application_operation.ParkFlow.exception.HandleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ValidUtils {

    private final UserDao userDao;
    private final ParkingDao parkingDao;

    // 信箱
    public void emailHasNotRegistered(String email) {
        // 信箱是否已經註冊過
        if(userDao.existEmail(email)){
            throw new HandleException("此信箱已經被註冊過。");
        }
    }

//    public void emailHasNotRegisteredExceptSelf(Integer id, String email){
//
//        // 信箱是否已經被自己以外的人註冊過
//        if(userDao.existEmailExceptSelf(id, email)){
//            throw new HandleException("信箱不能和此資料以外的內容重複。");
//        }
//    }

    // 日期
    public void notAfterToday(LocalDateTime inputDate){

        LocalDateTime today = LocalDateTime.now();
        // 日期是否在今天以後
        if(inputDate.isBefore(today)){
            throw new HandleException("下週開始日期必須是在今天之後的日期。");
        }
    }

    // 日期
    public void dateNotRepeat(LocalDateTime inputDate){

        // 日期是否有重複
        if(parkingDao.existsByWeekStartDate(inputDate)){
            throw new HandleException("日期資料不能重複。");
        }
    }

    // 日期
    public void dateNotRepeatExceptSelf(Integer id, LocalDateTime inputDate){

        // 日期是否有和自己以外的資料重複
        if(parkingDao.existsByWeekStartDateExceptSelf(id, inputDate)){
            throw new HandleException("日期資料不能和此資料以外的內容重複。");
        }
    }

    // 停車上限資料不存在
    public void existsByParkingQuotaId(Integer id){

        // 資料庫是否有對應的資料
        if(!parkingDao.existsByParkingQuotaId(id)){
            throw new HandleException("資料庫找不到 id 對應的內容。");
        }
    }

    // 使用者資料不存在
//    public void existsByUserId(Integer id){
//        // 資料庫是否有對應的資料
//        if(!userDao.existsByUserId(id)){
//            throw new HandleException("資料庫找不到 id 對應的內容。");
//        }
//    }

    // 身分驗證 - FM
    public void isFM(String roleName){
        if(!roleName.equals(RoleNameEnum.FM.name())) {
            throw new HandleException("權限驗證錯誤。");
        }
    }
}