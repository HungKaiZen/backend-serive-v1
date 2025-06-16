package vn.tayjava.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserChangePasswordRequest {
    @NotNull(message = "userId must be not null")
    @Min(value = 1, message = "userId greater than or equal 1")
    private Long userId;

    @NotBlank(message = "newPassword must be not blank")
    private String newPassword;

    @NotBlank(message = "confirmPassword must be not blank")
    private String confirmPassword;
}
