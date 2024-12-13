package Fream_back.improve_Fream_Back.address.dto;

import lombok.Data;

@Data
public class AddressUpdateDto {
    private Long addressId;
    private String recipientName;
    private String phoneNumber;
    private String zipCode;
    private String address;
    private String detailedAddress;
    private Boolean isDefault;
}
