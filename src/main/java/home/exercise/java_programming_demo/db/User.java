package home.exercise.java_programming_demo.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    @Exclude
    private UUID id;
    private String userName;
    @Embedded
    private Address address;
    private String email;
    private String telephone;
    private String password;
    @Embedded
    private UserProfile profile;
    @Enumerated(EnumType.STRING)
    private UserRole role;

}
