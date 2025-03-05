package application_operation.ParkFlow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateOutputDto {
    private Integer id;
    private String chineseName;
    private String englishName;
    private String email;
    private String cellphone;
    private String carNumber;
    private String carType;
}
