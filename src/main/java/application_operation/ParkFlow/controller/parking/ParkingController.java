package application_operation.ParkFlow.controller.parking;

import application_operation.ParkFlow.Response.SuccessResponse;
import application_operation.ParkFlow.controller.parking.payload.ParkinRequestCreateRq;
import application_operation.ParkFlow.controller.parking.payload.ParkingRequestUpdateRq;
import application_operation.ParkFlow.dto.parking.create.ParkingRequestDto;
import application_operation.ParkFlow.service.parking.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "v1/parking/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParkingController {
    private final ParkingService parkingService;

    @Operation(summary = "申請停車位" ,description = "申請停車位")
    @PostMapping(value = "create")
    public ResponseEntity<SuccessResponse<Object>> create(@Valid @RequestBody ParkinRequestCreateRq parkinRequestCreateRq) {
        ParkingRequestDto parkingRequestDto = parkingService.create(parkinRequestCreateRq);

        return ResponseEntity.ok(SuccessResponse.builder()
                .data(parkingRequestDto)
                .build());
    }

    @Operation(summary = "審核停車位" ,description = "審核停車位")
    @PutMapping(value = "update")
    public ResponseEntity<SuccessResponse<Object>> update(@Valid @RequestBody ParkingRequestUpdateRq parkingRequestUpdateRq) {
        parkingService.update(parkingRequestUpdateRq);

        return ResponseEntity.ok(SuccessResponse.builder()
                .data("")
                .build());
    }
}
