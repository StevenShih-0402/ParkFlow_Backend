package application_operation.ParkFlow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum {
    SUCCESS("0000"),
    REGISTER_REQ("0001");

    private final String responseCode;
}
