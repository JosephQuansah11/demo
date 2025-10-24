package home.exercise.java_programming_demo.akka;

import org.springframework.context.ApplicationContext;

// import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
// import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Behaviors;
import home.exercise.java_programming_demo.db.User;
import home.exercise.java_programming_demo.db.service.UserServiceRepository;

    public class RootActor {
    public static Behavior<User> create() {
        return Behaviors.setup(context -> {
            // Create the child actor
            // ActorRef<User> childActor = context.spawn(UserActor.create(), "userActor");
           context.getLog().info("Root actor started"); 
            // Return the behavior
            return Behaviors.empty();
        });
    }

     // The create method now accepts the Spring ApplicationContext
    public static Behavior<UserActorProtocol> create(ApplicationContext springContext) {
        return Behaviors.setup(context -> {
            context.getLog().info("Church Member Actor System started!");

            // Get a Spring bean from the context
            UserServiceRepository userService = springContext.getBean(UserServiceRepository.class);
            
            // Spawn a child actor, passing the Spring bean to it
            context.spawn(
                UserActor.create(userService), 
                "userProcessorActor"
            );

            // userProcessor.tell(new UserActorProtocol.ProcessUser(new User(), null));
            // // Now you can tell the child actor to do work.
            // // For example, you could watch for messages and forward them.
            // // For this example, the guardian's job is done after spawning the child.
            return UserActor.create(userService);
            // return Behaviors.empty(); // Guardian can ignore messages
        });
    }
}
