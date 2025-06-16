package vn.tayjava.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserType {
    ADMIN,
    MEMBER,
    USER;

    @JsonCreator
    public static UserType fromString(String value) {
        return UserType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String value() {
        return this.name();
    }


}
