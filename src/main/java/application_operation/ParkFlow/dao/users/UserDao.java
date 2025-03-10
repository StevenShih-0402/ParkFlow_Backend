package application_operation.ParkFlow.dao.users;

import application_operation.ParkFlow.dto.user.create.UserCreateDto;
import application_operation.ParkFlow.dto.user.create.UserLoginDto;
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



    @Transactional
    public UserEntity saveUser(UserCreateDto userCreateDto){
        UserEntity userEntity = convertToEntity(userCreateDto);
        return usersRepository.save(userEntity);
    }

    public UserEntity queryUserByEmail(UserLoginDto userLoginDto){
        return usersRepository.findByEmail(userLoginDto.getEmail());
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
}
