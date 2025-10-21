package home.exercise.java_programming_demo;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class FakerIntegrationTest {

    @Test
    void testFakerIntegration() {
        Faker faker = new Faker();
        
        // Test basic Faker functionality
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String phone = faker.phoneNumber().phoneNumber();
        String city = faker.address().city();
        String country = faker.address().country();
        
        // Verify that Faker generates non-null, non-empty values
        assertNotNull(firstName);
        assertNotNull(lastName);
        assertNotNull(email);
        assertNotNull(phone);
        assertNotNull(city);
        assertNotNull(country);
        
        assertFalse(firstName.isEmpty());
        assertFalse(lastName.isEmpty());
        assertFalse(email.isEmpty());
        assertFalse(phone.isEmpty());
        assertFalse(city.isEmpty());
        assertFalse(country.isEmpty());
        
        // Test that email contains @ symbol
        assertTrue(email.contains("@"));
        
        System.out.println("✅ Faker Integration Test Results:");
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
        System.out.println("City: " + city);
        System.out.println("Country: " + country);
    }
    
    @Test
    void testRealisticDataGeneration() {
        Faker faker = new Faker();
        
        // Test realistic church name generation
        String churchName = faker.company().name() + " Baptist Church";
        String pastorName = "Pastor " + faker.name().firstName() + " " + faker.name().lastName();
        
        // Test realistic department data
        String description = faker.lorem().sentence(8, 4);
        double budget = faker.number().randomDouble(2, 5000, 50000);
        int memberCount = faker.number().numberBetween(5, 50);
        
        assertNotNull(churchName);
        assertNotNull(pastorName);
        assertNotNull(description);
        assertTrue(budget >= 5000 && budget <= 50000);
        assertTrue(memberCount >= 5 && memberCount <= 50);
        
        System.out.println("✅ Realistic Data Generation Test Results:");
        System.out.println("Church: " + churchName);
        System.out.println("Pastor: " + pastorName);
        System.out.println("Description: " + description);
        System.out.println("Budget: $" + String.format("%.2f", budget));
        System.out.println("Members: " + memberCount);
    }
}
