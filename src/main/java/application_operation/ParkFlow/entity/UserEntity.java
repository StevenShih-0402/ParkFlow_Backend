package application_operation.ParkFlow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "USERS")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    // 指定我們自己建立的 Sequence
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "SEQ_USERS", allocationSize = 1)
    private Integer id;

    @Column(name = "CHINESE_NAME")
    private String chineseName;

    @Column(name = "ENGLISH_NAME")
    private String englishName;

    @Column(name = "EMAIL", updatable = false)
    private String email;

    @Column(name = "CELLPHONE")
    private String cellphone;

    @Column(name = "CAR_NUMBER")
    private String carNumber;

    @Column(name = "CAR_TYPE")
    private String carType;

    @Column(name = "ROLE_ID", insertable = false)
    private Integer roleId = 1;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "UPDATED_AT", insertable = false)
    private LocalDateTime updateAt;
}
