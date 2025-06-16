package vn.tayjava.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    NONE,
    BLOCKED;

    @JsonCreator
    public static UserStatus fromString(String value) {
        return UserStatus.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String value() {
        return this.name();
    }

}
