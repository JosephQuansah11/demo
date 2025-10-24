// package home.exercise.java_programming_demo.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import com.typesafe.config.Config;
// import com.typesafe.config.ConfigFactory;

// import akka.actor.typed.ActorSystem;
// import home.exercise.java_programming_demo.akka.RootActor;
// import home.exercise.java_programming_demo.db.User;

// @Configuration
// public class AkkaConfig {
//     @Bean
//     public static ActorSystem<User> actorSystem() {
//         Config config = ConfigFactory.load();
//         return ActorSystem.create(RootActor.create(),
//          "church-member-system",
//           config);
//     }
// }
