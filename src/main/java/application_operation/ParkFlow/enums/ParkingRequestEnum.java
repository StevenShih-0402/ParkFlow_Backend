package application_operation.ParkFlow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum ParkingRequestEnum {
    APPROVED("0"),
    REVIEWING("1"),
    REJECTED("2");

    private final String code;
}
