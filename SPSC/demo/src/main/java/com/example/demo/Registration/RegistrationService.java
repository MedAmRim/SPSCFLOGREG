package com.example.demo.Registration;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.ApplicationUser.AppUserRole;
import com.example.demo.ApplicationUser.ApplicationUser;
import com.example.demo.ApplicationUser.ApplicationUserService;
import com.example.demo.Registration.Token.ConfirmationToken;
import com.example.demo.Registration.Token.ConfirmationTokenService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final ApplicationUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    //private final EmailSender emailSender;

    public String register(RegistrationRequest request) {
        
        boolean isValidEmail = emailValidator.
                     test(request.getEmail());

        if (!isValidEmail) {
           throw new IllegalStateException("email not valid");
        }

        String token = appUserService.signUpUser(
                new ApplicationUser(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.USER

                )
        );

        String link = "http://localhost:8080/api/v1/registration/confirm?token=" + token;
        //emailSender.send(
             //   request.getEmail(),
              //  buildEmail(request.getFirstName(), link));

        return token;
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getApplicationUser().getEmail());
        return "confirmed";
    }
    
}
