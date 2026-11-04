package com.bluebells.staff;

import com.bluebells.common.PageResponse;
import com.bluebells.exception.ApiException;
import com.bluebells.exception.ResourceNotFoundException;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.staff.dto.CreateStaffRequest;
import com.bluebells.staff.dto.StaffDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    public StaffService(StaffRepository staffRepository, UserRepository userRepository) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<StaffDto> getStaff(Pageable pageable) {
        Page<Staff> page = staffRepository.findAll(pageable);
        return PageResponse.of(page.map(StaffDto::fromEntity));
    }

    @Transactional
    public StaffDto createStaff(CreateStaffRequest request) {
        if (staffRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new ApiException("Employee ID '" + request.getEmployeeId() + "' already exists", HttpStatus.CONFLICT);
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Staff staff = new Staff();
        staff.setUser(user);
        staff.setEmployeeId(request.getEmployeeId());
        staff.setDesignation(request.getDesignation());
        staff.setDepartment(request.getDepartment());
        staff.setQualification(request.getQualification());
        staff.setJoiningDate(request.getJoiningDate());
        staff.setSalary(request.getSalary());

        Staff saved = staffRepository.save(staff);
        return StaffDto.fromEntity(saved);
    }
}
