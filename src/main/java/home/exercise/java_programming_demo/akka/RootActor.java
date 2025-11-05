package home.exercise.java_programming_demo.akka;

import org.springframework.context.ApplicationContext;

import akka.actor.typed.ActorRef;
// import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
// import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Behaviors;
// import home.exercise.java_programming_demo.db.User;
import home.exercise.java_programming_demo.db.service.UserServiceRepository;

    public class RootActor {
    // public static Behavior<User> create() {
    //     return Behaviors.setup(context -> {
    //         // Create the child actor
    //         // ActorRef<User> childActor = context.spawn(UserActor.create(), "userActor");
    //        context.getLog().info("Root actor started"); 
    //         // Return the behavior
    //         return Behaviors.empty();
    //     });
    // }

     // The create method now accepts the Spring ApplicationContext
    public static Behavior<UserActorProtocol> create(ApplicationContext springContext) {
        return Behaviors.setup(context -> {
            context.getLog().info("Church Member Actor System started!");

            // Get a Spring bean from the context
            UserServiceRepository userService = springContext.getBean(UserServiceRepository.class);
            
            // Spawn the child UserActor
            ActorRef<UserActorProtocol> userProcessor = context.spawn(
                UserActor.create(userService),
                "userProcessorActor"
            );

            // Define the behavior to forward messages to the child actor
            return Behaviors.receive(UserActorProtocol.class)
            .onMessage(UserActorProtocol.ProcessUser.class, command -> {
                userProcessor.tell(command); // Forward the message to the child actor
                return Behaviors.same();
            })
            .onMessage(UserActorProtocol.UserProcessed.class, response -> {
                context.getLog().info("User processed: {}", response.user());
                return Behaviors.same();
            })
            .onMessage(UserActorProtocol.UserProcessingFailed.class, failure -> {
                context.getLog().error("Failed to process user: {}", failure.exception().getMessage());
                return Behaviors.same();
            })
            .build();
        });
    }
}
