package com.bluebells.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<SupportTicket> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<SupportTicket> findByStatusOrderByCreatedAtDesc(String status);

    List<SupportTicket> findByAssignedDepartmentOrderByCreatedAtDesc(String department);

    Optional<SupportTicket> findByTicketNumber(String ticketNumber);

    long countByStatus(String status);
}
