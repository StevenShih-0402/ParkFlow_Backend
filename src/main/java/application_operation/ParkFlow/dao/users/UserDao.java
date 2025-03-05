package application_operation.ParkFlow.dao.users;

import application_operation.ParkFlow.dto.UserCreateDto;
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

    public Boolean existCellphone(String cellphone){
        return usersRepository.existsByCellphone(cellphone);
    }

    @Transactional
    public UserCreateDto saveUser(UserCreateDto userCreateDto){
        UserEntity userEntity = convertToEntity(userCreateDto);
        UserEntity saveEntity = usersRepository.save(userEntity);

        return convertToDto(saveEntity);
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

    public UserCreateDto convertToDto(UserEntity userEntity){
        UserCreateDto userCreateDto = new UserCreateDto();

        userCreateDto.setChineseName(userEntity.getChineseName());
        userCreateDto.setEnglishName(userEntity.getEnglishName());
        userCreateDto.setEmail(userEntity.getEmail());
        userCreateDto.setCellphone(encodeCellphone(userEntity.getCellphone()));
        userCreateDto.setCarNumber(encodeCarNumber(userEntity.getCarNumber()));
        userCreateDto.setCarType(userEntity.getCarType());

        return userCreateDto;
    }

    public String encodeCarNumber(String carNumber){
        String firstChar = "";
        String lastChar = "";

        if (carNumber != null && carNumber.length() > 2) {
            firstChar = carNumber.substring(0, 1); // 取第一個字
            lastChar = carNumber.substring(carNumber.length() - 1); // 取最後一個字
        }
        return firstChar + "****" + lastChar; // 中間四個字為 "****"
    }

    public String encodeCellphone(String cellphone){

        String firstChar = cellphone.substring(0, 3); // 取前三個號碼
        String lastChar = cellphone.substring(cellphone.length() - 3); // 取後三個號碼
        return firstChar + "****" + lastChar; // 中間四個字為 "****"
    }
}
