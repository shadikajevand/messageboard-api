package org.kajevand.messageboard.exception;

public class NotAuthorizedException extends RuntimeException {

    public NotAuthorizedException() {
        super();
    }

    public NotAuthorizedException(Long messageId) {
        super("Current user is not authorized to modify message with id: " + messageId);
    }
}
