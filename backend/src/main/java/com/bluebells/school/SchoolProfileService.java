package com.bluebells.school;

import com.bluebells.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SchoolProfileService {

    private final SchoolProfileRepository schoolProfileRepository;

    public SchoolProfileService(SchoolProfileRepository schoolProfileRepository) {
        this.schoolProfileRepository = schoolProfileRepository;
    }

    @Transactional(readOnly = true)
    public SchoolProfileDto getProfile() {
        SchoolProfile profile = schoolProfileRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolProfile", "id", 1L));
        return SchoolProfileDto.fromEntity(profile);
    }

    @Transactional
    public SchoolProfileDto updateProfile(SchoolProfileDto dto) {
        SchoolProfile profile = schoolProfileRepository.findById(1L)
                .orElse(new SchoolProfile());

        profile.setId(1L);
        profile.setName(dto.getName());
        profile.setAddress(dto.getAddress());
        profile.setEmail(dto.getEmail());
        profile.setPhone(dto.getPhone());
        profile.setLogoUrl(dto.getLogoUrl());
        profile.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "PKR");
        profile.setTimezone(dto.getTimezone() != null ? dto.getTimezone() : "Asia/Karachi");
        profile.setUpdatedAt(Instant.now());

        SchoolProfile saved = schoolProfileRepository.save(profile);
        return SchoolProfileDto.fromEntity(saved);
    }
}
