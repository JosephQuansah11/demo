package home.exercise.java_programming_demo.akka;

import home.exercise.java_programming_demo.db.User;
import akka.actor.typed.ActorRef;

public interface UserActorProtocol {
    // record ProcessUser(User user, ActorRef<UserProcessed> replyTo) implements UserActorProtocol {}
    record ProcessUser(User user, ActorRef<UserActorProtocol> replyTo) implements UserActorProtocol {}
    record UserProcessed(User user) implements UserActorProtocol {}
    record UserProcessingFailed(User user, Exception exception) implements UserActorProtocol {}
}
