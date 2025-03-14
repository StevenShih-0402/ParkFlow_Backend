package application_operation.ParkFlow.controller.users.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRq {

    @NotBlank
    @Size(max = 20, min = 1)
    @Pattern(regexp = "^\\p{IsHan}+$", message = "中文姓名欄位只能填入中文。")
    @Schema(title = "中文姓名", example = "小吳")
    private String chineseName;

    @NotBlank
    @Size(max = 50, min = 1)
    @Pattern(regexp = "^[A-Za-z\\- ]+$", message = "英文姓名欄位只能填入英文、空格和連接線。")
    @Schema(title = "英文姓名", example = "Wu")
    private String englishName;

    @NotBlank
    @Pattern(regexp = "^09\\d{8}$", message = "行動電話必須為台灣電話號碼格式 (09開頭，共10個數字)。")
    @Schema(title = "電話號碼", example = "0912567456")
    private String cellphone;

    @NotBlank
    @Schema(title = "車牌號碼", example = "DAO-3458")
    private String carNumber;

    @NotBlank
    @Schema(title = "車型", example = "TOYOTA")
    private String carType;
}
