package vn.tayjava.dto.request;


import lombok.Getter;
import vn.tayjava.model.UserEntity;

@Getter
public class AddressRequest {
    private String apartmentNumber;
    private String floor;
    private String building;
    private String streetNumber;
    private String street;
    private String city;
    private String country;
    private Integer addressType;

}
