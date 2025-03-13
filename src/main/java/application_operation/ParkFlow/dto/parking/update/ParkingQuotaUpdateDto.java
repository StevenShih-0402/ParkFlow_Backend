package application_operation.ParkFlow.dto.parking.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingQuotaUpdateDto {
    private Integer id;
    private Integer totalSlots;
}
