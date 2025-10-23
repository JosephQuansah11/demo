package home.exercise.java_programming_demo.core;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import home.exercise.java_programming_demo.db.Church;
import home.exercise.java_programming_demo.db.service.ChurchRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
@Service

public class ChurchManagementService implements ChurchService {

    private final ChurchRepository churchRepository;

    @Override
    public List<Church> getAllChurches() {
        return churchRepository.findAll();
    }

    @Override
    public Church getChurchById(UUID id) {
        return churchRepository.findById(id).orElse(null);
    }

    @Override
    public void addChurch(Church church) {
        churchRepository.save(church);
    }

    @Override
    public void removeChurch(UUID id) {
        churchRepository.deleteById(id);
        
    }

    @Override
    public void updateChurch(Church church) {
        churchRepository.save(church);
    }
}
