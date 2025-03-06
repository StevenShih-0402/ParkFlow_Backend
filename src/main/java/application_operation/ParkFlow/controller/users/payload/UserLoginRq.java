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
public class UserLoginRq implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    @Email
    @Schema(title = "信箱", example = "lin@gmail.com")
    private String email;
}
