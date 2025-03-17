package application_operation.ParkFlow.validTag.cellphone;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CellphoneValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Cellphone {
    String message() default "行動電話必須為台灣電話號碼格式 (09開頭，共10個數字)。";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
