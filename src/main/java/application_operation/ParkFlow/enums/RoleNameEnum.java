package application_operation.ParkFlow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RoleNameEnum {
    USER(1),
    FM(2);

    private final Integer role;

    public static String getRoleNameById(Integer roleId) {
        return Arrays.stream(RoleNameEnum.values())  // 把所有 Enum 轉成 Stream
                .filter(role -> role.getRole().equals(roleId))  // 找出符合 roleId 的 Enum
                .map(Enum::name) // 將 Enum 轉成對應的名字(USER 或 FM)
                .findFirst()  // 取第一個符合條件的值
                .orElse(null); // 如果找不到則回傳 null
    }
}
