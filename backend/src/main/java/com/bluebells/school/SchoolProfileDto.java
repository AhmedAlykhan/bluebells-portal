package com.bluebells.school;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SchoolProfileDto {

    private Long id;

    @NotBlank(message = "School name is required")
    @Size(max = 128, message = "School name must not exceed 128 characters")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(max = 32, message = "Phone number must not exceed 32 characters")
    private String phone;

    private String logoUrl;

    @NotBlank(message = "Currency code is required")
    @Size(max = 8, message = "Currency code must not exceed 8 characters")
    private String currency;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    public SchoolProfileDto() {
    }

    public SchoolProfileDto(Long id, String name, String address, String email, String phone, String logoUrl, String currency, String timezone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.logoUrl = logoUrl;
        this.currency = currency;
        this.timezone = timezone;
    }

    public static SchoolProfileDto fromEntity(SchoolProfile entity) {
        if (entity == null) return null;
        return new SchoolProfileDto(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getLogoUrl(),
                entity.getCurrency(),
                entity.getTimezone()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
