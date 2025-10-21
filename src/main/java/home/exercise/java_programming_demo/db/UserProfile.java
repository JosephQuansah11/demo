package home.exercise.java_programming_demo.db;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private String firstName;
    private String lastName;
    @Embedded
    private UserPreferences preferences;
}
