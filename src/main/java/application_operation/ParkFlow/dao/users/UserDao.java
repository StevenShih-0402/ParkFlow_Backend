package application_operation.ParkFlow.dao.users;

import application_operation.ParkFlow.dto.UserCreateDto;
import application_operation.ParkFlow.dto.UserCreateOutputDto;
import application_operation.ParkFlow.entity.UserEntity;
import application_operation.ParkFlow.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserDao {

    private final UsersRepository usersRepository;

    public Boolean existEmail(String email){
        return usersRepository.existsByEmail(email);
    }

//    public Boolean existCellphone(String cellphone){
//        return usersRepository.existsByCellphone(cellphone);
//    }

    @Transactional
    public UserEntity saveUser(UserCreateDto userCreateDto){
        UserEntity userEntity = convertToEntity(userCreateDto);
        UserEntity saveEntity = usersRepository.save(userEntity);

        saveEntity.setCellphone(encodeCellphone(saveEntity.getCellphone()));
        saveEntity.setCarNumber(encodeCarNumber(saveEntity.getCarNumber()));

        return saveEntity;

//        return convertToDto(saveEntity);
    }

    public UserEntity convertToEntity(UserCreateDto userCreateDto){
        UserEntity userEntity = new UserEntity();

        userEntity.setChineseName(userCreateDto.getChineseName());
        userEntity.setEnglishName(userCreateDto.getEnglishName());
        userEntity.setEmail(userCreateDto.getEmail());
        userEntity.setCellphone(userCreateDto.getCellphone());
        userEntity.setCarNumber(userCreateDto.getCarNumber());
        userEntity.setCarType(userCreateDto.getCarType());

        return userEntity;
    }

//    public UserCreateOutputDto convertToDto(UserEntity userEntity){
//        UserCreateOutputDto userCreateOutputDto = new UserCreateOutputDto();
//
//        userCreateOutputDto.setId(userEntity.getId());
//        userCreateOutputDto.setChineseName(userEntity.getChineseName());
//        userCreateOutputDto.setEnglishName(userEntity.getEnglishName());
//        userCreateOutputDto.setEmail(userEntity.getEmail());
//        userCreateOutputDto.setCellphone(encodeCellphone(userEntity.getCellphone()));
//        userCreateOutputDto.setCarNumber(encodeCarNumber(userEntity.getCarNumber()));
//        userCreateOutputDto.setCarType(userEntity.getCarType());
//
//        return userCreateOutputDto;
//    }

    public String encodeCarNumber(String carNumber){
        String firstChar = carNumber.substring(0, 1); // 取第一個字
        String lastChar = carNumber.substring(carNumber.length() - 1); // 取最後一個字
        return firstChar + "****" + lastChar; // 中間四個字為 "****"
    }

    public String encodeCellphone(String cellphone){

        String firstChar = cellphone.substring(0, 3); // 取前三個號碼
        String lastChar = cellphone.substring(cellphone.length() - 3); // 取後三個號碼
        return firstChar + "****" + lastChar; // 中間四個字為 "****"
    }
}
