package home.exercise.java_programming_demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.UUID;
import home.exercise.java_programming_demo.db.Church;
import home.exercise.java_programming_demo.core.ChurchService;

@RestController
@RequestMapping("/churches")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@Log4j2
@RestControllerAdvice
public class ChurchController {
    private final ChurchService churchService;
    
    @GetMapping("/all")
    public ResponseEntity<List<Church>> getAllChurches() {
        return ResponseEntity.ok(churchService.getAllChurches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Church> getChurchById(@PathVariable("id") String id) {
        return ResponseEntity.ok(churchService.getChurchById(UUID.fromString(id)));
    }

    @PostMapping
    public ResponseEntity<Church> addChurch(@RequestBody Church church) {
        log.info("Adding church: {}", church);
        churchService.addChurch(church);
        return ResponseEntity.ok(church);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Church> removeChurch(@PathVariable("id") String id) {
        churchService.removeChurch(UUID.fromString(id));
        return ResponseEntity.ok(churchService.getChurchById(UUID.fromString(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Church> updateChurch(@PathVariable("id") String id, @RequestBody Church church) throws Exception{
        ResponseEntity<Church> churchId = getChurchById(id);
        if(churchId != null){
            churchService.updateChurch(church);
            return ResponseEntity.ok(church);
        }else{
            throw new Exception("Cannot update church");
        }
    } 
}
