package org.kajevand.messageboard.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "message")
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max=255, message="Subject can not be longer than 255 characters")
    @NotBlank(message = "Subject is mandatory")
    private String subject;

    @Size(max=1000, message="Content can not be longer than 1000 characters")
    @NotBlank(message = "Content is mandatory")
    private String content;

    @Size(max=255, message="Content can not be longer than 255 characters")
    @NotBlank(message = "User name is mandatory")
    @Email(message = "Username should be a valid Email address")
    private String username;
}