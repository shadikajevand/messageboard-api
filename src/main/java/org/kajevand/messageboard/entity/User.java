package org.kajevand.messageboard.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @Size(max=255, message="Content can not be longer than 255 characters")
    @NotBlank(message = "User name is mandatory")
    @Email(message = "Username should be a valid Email address")
    private String username;
    @NotBlank(message = "Password is mandatory")
    private String password;
}
