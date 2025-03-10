package application_operation.ParkFlow.dao.users;

import application_operation.ParkFlow.dto.UsersBaseDto;
import application_operation.ParkFlow.dto.mail.SendEmailDto;
import application_operation.ParkFlow.dto.parking.queryUserParkingRequest.QueryUserAndRoleDto;
import application_operation.ParkFlow.dto.users.create.UserCreateDto;
import application_operation.ParkFlow.dto.users.create.UserLoginDto;
import application_operation.ParkFlow.entity.UserEntity;
import application_operation.ParkFlow.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public QueryUserAndRoleDto findUsersAndRoleById(UsersBaseDto usersBaseDto) {

        List<Object[]> users = usersRepository.findUsersAndRoleById(usersBaseDto.getUserId());

        String roleName = (String) users.get(0)[0];
        String englishName = (String) users.get(0)[1];
        String email = (String) users.get(0)[2];

        return QueryUserAndRoleDto.builder().roleName(roleName).englishName(englishName).email(email).build();
    }

    @Transactional(readOnly = true)
    public SendEmailDto queryFMEmailData () {
        List<Object[]> list = usersRepository.findUsersAndRoleByFM();

        return SendEmailDto.builder()
                .name((String) list.get(0)[0])
                .email((String) list.get(0)[1])
                .build();
    }
}
