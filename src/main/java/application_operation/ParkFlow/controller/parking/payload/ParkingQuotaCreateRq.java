package application_operation.ParkFlow.controller.parking.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingQuotaCreateRq {

    @NotNull
    @Schema(title = "下週開始日期", example = "2025-03-17")
    private LocalDate nextWeekStartDate;

    @NotNull
    @Schema(title = "可申請的車位數量", example = "10")
    private Integer totalSlots;
}
