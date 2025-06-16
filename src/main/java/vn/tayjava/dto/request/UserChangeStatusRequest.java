package vn.tayjava.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import vn.tayjava.common.UserStatus;
import vn.tayjava.dto.validation.ValidEnum;

@Getter
public class UserChangeStatusRequest {
    @NotNull(message = "userId must be not null")
    private Long userId;

    @NotNull(message = "status must be not null")
    @ValidEnum(name = "status", regexp = "ACTIVE|INACTIVE|NONE|BLOCKED")
    private UserStatus status;
}
