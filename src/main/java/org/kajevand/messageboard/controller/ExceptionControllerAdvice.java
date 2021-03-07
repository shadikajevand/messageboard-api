package org.kajevand.messageboard.controller;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.java.Log;
import org.kajevand.messageboard.exception.MessageNotFoundException;
import org.kajevand.messageboard.exception.NotAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.naming.AuthenticationException;
import java.util.logging.Level;

@ControllerAdvice
@Log
public class ExceptionControllerAdvice {

    @ResponseBody
    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String messageNotFoundHandler(MessageNotFoundException ex) {
        log.log(Level.SEVERE, "Message not found", ex);
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String notAuthorizedHandler(NotAuthorizedException ex) {
        log.log(Level.SEVERE, "Not Authorized", ex);
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String invalidAuthenticationHandler(AuthenticationException ex) {
        log.log(Level.SEVERE, "Invalid authentication", ex);
        return "Invalid username or password";
    }

    @ResponseBody
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String jwtExpiredHandler(ExpiredJwtException ex) {
        log.log(Level.WARNING, "Expired JWT", ex);
        return "Authentication expired";
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String exceptionHandler(Exception ex) {
        log.log(Level.SEVERE, "General exception", ex);
        return ex.getMessage();
    }
}
