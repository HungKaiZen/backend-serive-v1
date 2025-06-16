package vn.tayjava.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import vn.tayjava.common.Platform;

@Getter
public class SignInRequest {

    @NotBlank(message = "username must be not blank")
    private String username;
    @NotBlank(message = "password must be not blank")
    private String password;

    private Platform platform;

    private String deviceToken;

}
