package application_operation.ParkFlow.controller.users.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRq {

    @NotBlank
    @Schema(title = "中文姓名", example = "小吳")
    private String chineseName;

    @NotBlank
    @Schema(title = "英文姓名", example = "Wu")
    private String englishName;

    @Email
    @NotBlank
    @Schema(title = "信箱", example = "wu@gmail.com")
    private String email;

    @NotBlank
    @Schema(title = "電話號碼", example = "0912567456")
    private String cellphone;

    @NotBlank
    @Schema(title = "車牌號碼", example = "DAO-3458")
    private String carNumber;

    @NotBlank
    @Schema(title = "車型", example = "TOYOTA")
    private String carType;
}
