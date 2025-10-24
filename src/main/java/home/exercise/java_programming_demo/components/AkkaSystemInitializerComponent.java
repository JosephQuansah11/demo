package home.exercise.java_programming_demo.components;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.typesafe.config.ConfigFactory;

import akka.actor.typed.ActorSystem;
import home.exercise.java_programming_demo.akka.RootActor;
import home.exercise.java_programming_demo.akka.UserActorProtocol;
import jakarta.annotation.PreDestroy;

@Component
public class AkkaSystemInitializerComponent {
    private final ApplicationContext applicationContext;
    private ActorSystem<UserActorProtocol> actorSystem;
    
    public AkkaSystemInitializerComponent(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

     // We expose the ActorSystem as a bean so other services can inject it
    @Bean
    public ActorSystem<UserActorProtocol> actorSystem() {
        // The guardian is created here, passing the Spring context
        this.actorSystem = ActorSystem.create(
            RootActor.create(applicationContext), // Pass the context!
            "church-member-system",
            ConfigFactory.load() // Load default config
        );
        return this.actorSystem;
    }

    @PreDestroy
    public void terminate() {
        if (actorSystem != null) {
            actorSystem.terminate();
        }
    }
   
}
