package vn.tayjava.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_address")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity extends AbstractEntity<Long> {

    @Column(name = "apartment_number")
    private String apartmentNumber;

    private String floor;

    private String building;

    @Column(name = "street_number")
    private String streetNumber;

    private String street;

    private String city;

    private String country;

    @Column(name = "address_type")
    private Integer addressType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
