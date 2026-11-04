package com.bluebells.attendance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class RecordSectionAttendanceRequest {

    @NotNull(message = "Section ID is required")
    private Long sectionId;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @NotEmpty(message = "Attendance items cannot be empty")
    @Valid
    private List<AttendanceItemRequest> items;

    public RecordSectionAttendanceRequest() {
    }

    public RecordSectionAttendanceRequest(Long sectionId, LocalDate attendanceDate, List<AttendanceItemRequest> items) {
        this.sectionId = sectionId;
        this.attendanceDate = attendanceDate;
        this.items = items;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public List<AttendanceItemRequest> getItems() {
        return items;
    }

    public void setItems(List<AttendanceItemRequest> items) {
        this.items = items;
    }
}
