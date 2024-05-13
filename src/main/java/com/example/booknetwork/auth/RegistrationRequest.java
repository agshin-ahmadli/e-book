package com.example.booknetwork.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    @NotEmpty(message = "Firstname is mandatory")
    @NotBlank(message = "Firstname is mandatory")
    private String firstName;

    @NotBlank(message = "Lastname is mandatory")
    @NotEmpty(message = "Lastname is mandatory")
    private String lastName;

    @Email(message = "Email is not well formatted")
    @NotBlank(message = "Email is mandatory")
    @NotEmpty(message = "Email is mandatory")
    private String email;

    @Size(min = 8, message = "Password should be 8 characters long minimum")
    @NotBlank(message = "Password is mandatory")
    @NotEmpty(message = "Password is mandatory")
    private String password;
}
