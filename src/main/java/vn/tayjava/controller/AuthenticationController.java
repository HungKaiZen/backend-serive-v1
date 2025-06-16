package vn.tayjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.tayjava.dto.request.SignInRequest;
import vn.tayjava.dto.request.UserCreationRequest;
import vn.tayjava.dto.response.ApiResponseSuccess;
import vn.tayjava.service.AuthenticationService;
import vn.tayjava.service.UserService;

@RestController
@RequestMapping("/auth")
@Validated
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;


    @PostMapping("/access")
    public ApiResponseSuccess<?> login(@RequestBody SignInRequest request) {
        return new ApiResponseSuccess<>(HttpStatus.OK.value(), "Access token", authenticationService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ApiResponseSuccess<?> refresh(HttpServletRequest request) {
        return new ApiResponseSuccess<>(HttpStatus.OK.value(), "Refresh token", authenticationService.refresh(request));
    }

    @PostMapping("/logout")
    public String logout() {
        return "Logout successful";
    }


    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping(value="/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseSuccess<?> addUser(@Valid @RequestBody UserCreationRequest request) {
        log.info("Add user request: {}", request);
        return new ApiResponseSuccess<>(HttpStatus.OK.value(), "User added successfully", userService.saveUser(request));
    }
}
