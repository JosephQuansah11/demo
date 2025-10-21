package home.exercise.java_programming_demo.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Embedded;
import java.util.UUID;

@Entity
@Table(name = "churches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Church {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String denomination;
    @Embedded
    private Address address;
    private String email;
    private String telephone;
    private String pastor;
    private Integer capacity;
}
