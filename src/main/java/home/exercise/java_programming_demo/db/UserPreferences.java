package home.exercise.java_programming_demo.db;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    private String language;
    private String theme;
    private boolean notifications;
}
