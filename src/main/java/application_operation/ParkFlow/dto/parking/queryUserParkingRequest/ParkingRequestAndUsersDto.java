package application_operation.ParkFlow.dto.parking.queryUserParkingRequest;

import application_operation.ParkFlow.enums.ParkingRequestEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingRequestAndUsersDto {
    private LocalDateTime requestTime;
    private String chineseName;
    private String carType;
    private String carNumber;
    private String cellphone;
    private Integer parkingSlotNumber;
    private ParkingRequestEnum status;
}
