package home.exercise.java_programming_demo.core;

import java.util.List;
import java.util.UUID;

import home.exercise.java_programming_demo.db.Church;

public interface ChurchService {
    public List<Church> getAllChurches();
    public Church getChurchById(UUID id);
    public void addChurch(Church church);
    public void removeChurch(UUID id);
    public void updateChurch(Church church);
}
