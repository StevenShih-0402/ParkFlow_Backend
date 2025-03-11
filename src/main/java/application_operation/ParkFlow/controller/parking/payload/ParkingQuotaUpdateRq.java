package application_operation.ParkFlow.controller.parking.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingQuotaUpdateRq {

    @NotNull
    @Positive
    @Schema(title = "停車位數量上限的資料編號", example = "1")
    private Integer id;

    @NotNull
    @Schema(title = "下週開始日期", example = "2025-03-17")
    private LocalDate weekStartDate;

    @NotNull
    @Positive
    @Schema(title = "可申請的車位數量上限", example = "10")
    private Integer totalSlots;
}
