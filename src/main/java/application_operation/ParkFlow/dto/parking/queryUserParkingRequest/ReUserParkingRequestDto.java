package application_operation.ParkFlow.dto.parking.queryUserParkingRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ReUserParkingRequestDto {
    private List<parkingRequest> parkingRequestList;

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class parkingRequest {
        private LocalDateTime requestTime;
        private String chineseName;
        private String carType;
        private String carNumber;
        private String cellphone;
        private Integer parkingSlotNumber;
        private String status;
    }
}
