package home.exercise.java_programming_demo.db.service;

import home.exercise.java_programming_demo.db.Church;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChurchRepository extends JpaRepository<Church, UUID> {
}
