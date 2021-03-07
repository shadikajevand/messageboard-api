package org.kajevand.messageboard.exception;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(Long messageId) {
        super("Message with id: " + messageId + " not found");
    }
}
