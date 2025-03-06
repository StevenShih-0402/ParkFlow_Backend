package application_operation.ParkFlow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleNameEnum {
    USER(1),
    FM(2);

    private final Integer role;
}
