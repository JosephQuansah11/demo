package home.exercise.java_programming_demo.akka;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import home.exercise.java_programming_demo.db.User;
import home.exercise.java_programming_demo.db.service.UserServiceRepository;
import home.exercise.java_programming_demo.core.UserManagementService;

public class UserActor {
    public static Behavior<User> create(UserManagementService userManagementService) {
        return Behaviors.receiveMessage(user -> {
           userManagementService.getUserById(user.getId());
           return Behaviors.same();
        });
    }

     // The create method accepts the Spring dependency it needs
    public static Behavior<UserActorProtocol> create(UserServiceRepository userService) {
        return Behaviors.receive(UserActorProtocol.class)
            .onMessage(UserActorProtocol.ProcessUser.class, command -> {
                // Use the Spring-managed repository!
                User savedUser = userService.save(command.user());
                command.replyTo().tell(new UserActorProtocol.UserProcessed(savedUser));
                return Behaviors.same();
            })
            .build();
    }
}
