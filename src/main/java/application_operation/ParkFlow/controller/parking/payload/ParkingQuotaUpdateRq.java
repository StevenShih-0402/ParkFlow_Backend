package application_operation.ParkFlow.controller.parking.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingQuotaUpdateRq {

    @NotNull
    @Positive
    @Schema(title = "停車位數量上限的資料編號", example = "1")
    private Integer id;

    @NotNull
    @Positive
    @Schema(title = "可申請的車位數量上限", example = "10")
    private Integer totalSlots;
}
