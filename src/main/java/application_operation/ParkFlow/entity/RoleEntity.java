package application_operation.ParkFlow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ROLE")
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "ROLENAME")
    private String roleName;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updateAt;
}
