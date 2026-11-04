package com.bluebells.timetable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableSlot, Long> {

    List<TimetableSlot> findBySectionId(Long sectionId);

    List<TimetableSlot> findByTeacherId(Long teacherId);

    List<TimetableSlot> findBySectionIdAndAcademicSessionId(Long sectionId, Long sessionId);

    @Query("SELECT t FROM TimetableSlot t WHERE t.teacher.id = :teacherId " +
           "AND t.dayOfWeek = :dayOfWeek " +
           "AND t.startTime < :endTime AND t.endTime > :startTime")
    List<TimetableSlot> findTeacherClashes(Long teacherId, String dayOfWeek, LocalTime startTime, LocalTime endTime);

    @Query("SELECT t FROM TimetableSlot t WHERE t.section.id = :sectionId " +
           "AND t.dayOfWeek = :dayOfWeek " +
           "AND t.startTime < :endTime AND t.endTime > :startTime")
    List<TimetableSlot> findSectionClashes(Long sectionId, String dayOfWeek, LocalTime startTime, LocalTime endTime);

    @Query("SELECT t FROM TimetableSlot t WHERE t.roomNumber = :roomNumber " +
           "AND t.roomNumber IS NOT NULL AND t.roomNumber != '' " +
           "AND t.dayOfWeek = :dayOfWeek " +
           "AND t.startTime < :endTime AND t.endTime > :startTime")
    List<TimetableSlot> findRoomClashes(String roomNumber, String dayOfWeek, LocalTime startTime, LocalTime endTime);
}
