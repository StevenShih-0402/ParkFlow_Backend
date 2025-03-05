package application_operation.ParkFlow.controller.parking.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ParkinRequestCreateRq implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Schema(title = "當周開始時間", description = "當周開始時間", example = "2025-03-02T00:00:00")
    private LocalDateTime weekStartDate;

    @NotBlank
    @Schema(title = "電話號碼", description = "電話號碼")
    private String cellPhone;

    @NotBlank
    @Schema(title = "車牌號碼", description = "車牌號碼", example = "EAF-2200")
    private String carNumber;

    @NotBlank
    @Schema(title = "車子品牌", description = "車子品牌", example = "TOYOTA")
    private String carType;
}
