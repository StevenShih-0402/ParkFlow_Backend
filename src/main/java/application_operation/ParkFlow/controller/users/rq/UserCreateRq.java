package application_operation.ParkFlow.controller.users.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRq {

    @Schema(title = "中文姓名")
    private String chineseName;

    @Schema(title = "英文姓名")
    private String englishName;

    @Email
    @Schema(title = "信箱")
    private String email;

    @Schema(title = "電話號碼")
    private String cellphone;

    @Schema(title = "車牌號碼")
    private String carNumber;

    @Schema(title = "車型")
    private String carType;
}
