package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByStaffAccountEmail(String email);
    boolean existsByStaffAccountEmail(String email);

    @Query("select max(c.id) from Staff c")
    String lastId();
}
