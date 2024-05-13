package com.example.booknetwork.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(path = "/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        authenticationService.register(request);
        return  ResponseEntity.accepted().build();
    }

    @PostMapping(path = "/authenticate")
    public ResponseEntity <AuthenticationResponse> authenticate (@RequestBody @Valid AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping(path = "/activate-account")
    public void confirm(@RequestParam String token) throws MessagingException {
        authenticationService.activateAccount(token);
    }

}
