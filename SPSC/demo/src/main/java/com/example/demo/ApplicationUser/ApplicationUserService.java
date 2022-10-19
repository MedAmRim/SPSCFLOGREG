package com.example.demo.ApplicationUser;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Registration.Token.ConfirmationToken;
import com.example.demo.Registration.Token.ConfirmationTokenService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ApplicationUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG =
    "user with email %s not found";

    private final ApplicationUserRepository applicationUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return applicationUserRepository.findByEmail(username)
                                    .orElseThrow(
                                        () -> new UsernameNotFoundException(
                                        String.format(USER_NOT_FOUND_MSG,username)
                                    )
                                    );
    }

    public String signUpUser(ApplicationUser user) {

        boolean userExists = applicationUserRepository
                .findByEmail(user.getEmail())
                .isPresent();

        if (userExists) {
            // TODO check of attributes are the same and
            // TODO if email not confirmed send confirmation email.

            throw new IllegalStateException("email already taken");
        }

        
        String encodedPassword = bCryptPasswordEncoder
                .encode(user.getPassword());

        user.setPassword(encodedPassword);

        applicationUserRepository.save(user);

        String token = UUID.randomUUID().toString();

       ConfirmationToken confirmationToken = new ConfirmationToken(
               token,
               LocalDateTime.now(),
               LocalDateTime.now().plusMinutes(15),
               user
                     );

       confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

    public int enableAppUser(String email) {
        return applicationUserRepository.enableAppUser(email);
    }
    
}
