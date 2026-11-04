package com.bluebells.guardians.dto;

import com.bluebells.guardians.Guardian;

public class GuardianDto {

    private Long id;
    private Long userId;
    private String fullName;
    private String relationshipType;
    private String cnicNumber;
    private String phonePrimary;
    private String phoneSecondary;
    private String occupation;
    private String address;

    public GuardianDto() {
    }

    public static GuardianDto fromEntity(Guardian guardian) {
        if (guardian == null) return null;
        GuardianDto dto = new GuardianDto();
        dto.setId(guardian.getId());
        if (guardian.getUser() != null) {
            dto.setUserId(guardian.getUser().getId());
        }
        dto.setFullName(guardian.getFullName());
        dto.setRelationshipType(guardian.getRelationshipType());
        dto.setCnicNumber(guardian.getCnicNumber());
        dto.setPhonePrimary(guardian.getPhonePrimary());
        dto.setPhoneSecondary(guardian.getPhoneSecondary());
        dto.setOccupation(guardian.getOccupation());
        dto.setAddress(guardian.getAddress());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getCnicNumber() {
        return cnicNumber;
    }

    public void setCnicNumber(String cnicNumber) {
        this.cnicNumber = cnicNumber;
    }

    public String getPhonePrimary() {
        return phonePrimary;
    }

    public void setPhonePrimary(String phonePrimary) {
        this.phonePrimary = phonePrimary;
    }

    public String getPhoneSecondary() {
        return phoneSecondary;
    }

    public void setPhoneSecondary(String phoneSecondary) {
        this.phoneSecondary = phoneSecondary;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
