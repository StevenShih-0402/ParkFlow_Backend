package application_operation.ParkFlow.dto;

import application_operation.ParkFlow.jwtToken.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
public class UsersBaseDto {
    private String chineseName;
    private String englishName;
    private String email;
    private String cellphone;
    private String carNumber;
    private String carType;
    private Integer userId;
    private String roleName;
}
