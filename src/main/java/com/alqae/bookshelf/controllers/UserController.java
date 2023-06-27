package com.alqae.bookshelf.controllers;

import com.alqae.bookshelf.dto.*;
import com.alqae.bookshelf.exception.RequestException;
import com.alqae.bookshelf.models.*;
import com.alqae.bookshelf.repositories.RoleRepository;
import com.alqae.bookshelf.repositories.TokenRepository;
import com.alqae.bookshelf.repositories.UserRepository;
import com.alqae.bookshelf.response.ResponseHandler;
import com.alqae.bookshelf.security.jwt.JwtUtils;
import com.alqae.bookshelf.services.IEmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class UserController {
    @Value("${webapp.url}")
    private String webappUrl;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    IEmailService emailService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/sign-up")
    public ResponseEntity<Object> signUp(@Valid @RequestBody CreateUserDto createUserDto) {
        userRepository.findByEmail(createUserDto.getEmail())
                .ifPresent(userEntity -> {
                    throw new RequestException("User already exists", HttpStatus.INTERNAL_SERVER_ERROR);
                });

        Set<RoleEntity> roles = Stream.of(ERole.USER)
                .map(role -> RoleEntity.builder()
                        .name(role)
                        .build())
                .collect(Collectors.toSet());

        UserEntity userEntity = UserEntity.builder()
                .email(createUserDto.getEmail())
                .password(passwordEncoder.encode(createUserDto.getPassword()))
                .firstName(createUserDto.getFirstName())
                .lastName(createUserDto.getLastName())
                .status(EStatus.PENDING)
                .roles(roles)
                .build();

        userRepository.save(userEntity);

        String token = jwtUtils.generateAccessToken(userEntity.getEmail());
        TokenEntity tokenEntity = TokenEntity.builder()
                .token(token)
                .isActive(true)
                .type(ETokenType.VERIFY_EMAIL)
                .user(userEntity)
                .build();

        tokenRepository.save(tokenEntity);
        emailService.sendHtmlMessage(
                new String[] { userEntity.getEmail() },
                "Welcome to Bookshelf",
                EmailTemplates.VERIFY_EMAIL,
                Map.of(
                    "firstName", userEntity.getFirstName(),
                    "lastName", userEntity.getLastName(),
                    "link", String.format("%s/auth/sign-in?token=%s&type=%s", webappUrl, token, tokenEntity.getType())
                )
        );

        return ResponseHandler.responseBuilder("User created!", HttpStatus.OK, userEntity);
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@Valid @RequestBody String token) {
        TokenEntity tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RequestException("Token not found", HttpStatus.NOT_FOUND));

        if (!jwtUtils.isValid(tokenEntity.getToken()) && !tokenEntity.getType().equals(ETokenType.VERIFY_EMAIL)) {
            throw new RequestException("Invalid token or type", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserEntity userEntity = tokenEntity.getUser();

        if (userEntity.getStatus() == EStatus.ACTIVE) {
            throw new RequestException("User already verified", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        userEntity.setStatus(EStatus.ACTIVE);
        tokenEntity.setActive(false);
        userRepository.save(userEntity);
        tokenRepository.save(tokenEntity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-invitation")
    public ResponseEntity<Object> sendInvitation(@Valid @RequestBody CreateInvitationDto createInvitationDto) {
        userRepository.findByEmail(createInvitationDto.getEmail())
                .ifPresent(userEntity -> {
                    throw new RequestException("User already exists", HttpStatus.INTERNAL_SERVER_ERROR);
                });

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(createInvitationDto.getEmail());
        userEntity.setStatus(EStatus.INVITED);
        userRepository.save(userEntity);

        String token = jwtUtils.generateAccessToken(userEntity.getEmail());
        TokenEntity tokenEntity = TokenEntity.builder()
                .token(token)
                .isActive(true)
                .type(ETokenType.INVITE_USER)
                .user(userEntity)
                .build();
        tokenRepository.save(tokenEntity);

        emailService.sendHtmlMessage(
                new String[] { userEntity.getEmail() },
                "Has been invited to Bookshelf",
                EmailTemplates.INVITE_USER,
                Map.of(
                    "link", String.format("%s/auth/sign-in?token=%s&type=%s", webappUrl, token, tokenEntity.getType())
                )
        );

        return ResponseHandler.responseBuilder("Invitation sent!", HttpStatus.OK, null);
    }

    @PostMapping("/accept-invitation")
    public ResponseEntity<Object> acceptInvitation(@Valid @RequestBody AceptInvitationDto aceptInvitationDto) {
        TokenEntity tokenEntity = tokenRepository.findByToken(aceptInvitationDto.getToken())
                .orElseThrow(() -> new RequestException("Token not found", HttpStatus.NOT_FOUND));

        if (tokenEntity.getType() != ETokenType.INVITE_USER || !tokenEntity.isActive()) {
            throw new RequestException("Invalid token type", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserEntity userEntity = tokenEntity.getUser();

        if (userEntity.getStatus() == EStatus.ACTIVE) {
            throw new RequestException("User already verified", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        userEntity.setPassword(passwordEncoder.encode(aceptInvitationDto.getPassword()));
        userEntity.setFirstName(aceptInvitationDto.getFirstName());
        userEntity.setLastName(aceptInvitationDto.getLastName());
        userEntity.setStatus(EStatus.ACTIVE);
        tokenEntity.setActive(false);
        userRepository.save(userEntity);
        tokenRepository.save(tokenEntity);

        return ResponseHandler.responseBuilder("Invitation accepted!", HttpStatus.OK, null);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {
        UserEntity userEntity = userRepository.findByEmail(forgotPasswordDto.getEmail())
                .orElseThrow(() -> new RequestException("User not found", HttpStatus.NOT_FOUND));

        String token = jwtUtils.generateAccessToken(userEntity.getEmail());
        TokenEntity tokenEntity = TokenEntity.builder()
                .token(token)
                .isActive(true)
                .type(ETokenType.RESET_PASSWORD)
                .user(userEntity)
                .build();

        tokenRepository.save(tokenEntity);

        emailService.sendHtmlMessage(
                new String[] { userEntity.getEmail() },
                "Reset password",
                EmailTemplates.RESET_PASSWORD,
                Map.of(
                    "link", String.format("%s/auth/forgot-password?token=%s&type=%s", webappUrl, token, tokenEntity.getType())
                )
        );

        return ResponseHandler.responseBuilder("Reset password link sent!", HttpStatus.OK, null);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        TokenEntity tokenEntity = tokenRepository.findByToken(resetPasswordDto.getToken())
                .orElseThrow(() -> new RequestException("Token not found", HttpStatus.NOT_FOUND));

        if (tokenEntity.getType() != ETokenType.RESET_PASSWORD || !tokenEntity.isActive()) {
            throw new RequestException("Invalid token type", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserEntity userEntity = tokenEntity.getUser();
        userEntity.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        tokenEntity.setActive(false);
        userRepository.save(userEntity);
        tokenRepository.save(tokenEntity);

        return ResponseHandler.responseBuilder("Password reset!", HttpStatus.OK, null);
    }

    @DeleteMapping("/users")
    public ResponseEntity<Object> deleteUser(@RequestParam UUID id) {
        userRepository.deleteById(id);
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RequestException("User not found", HttpStatus.NOT_FOUND));
        userEntity.setStatus(EStatus.DELETED);
        userRepository.save(userEntity);
        return ResponseHandler.responseBuilder("User deleted!", HttpStatus.OK, null);
    }
}
