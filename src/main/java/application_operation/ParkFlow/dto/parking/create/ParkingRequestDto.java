package application_operation.ParkFlow.dto.parking.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingRequestDto {
    private Integer Id;
    private LocalDateTime applicationTime;
    private LocalDateTime weekStartDate;
    private String cellPhone;
    private String carNumber;
    private String carType;
}
