package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.StaffAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffAccountRepository extends JpaRepository<StaffAccount, Long> {
    Optional<StaffAccount> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);

    @Query("select max(c.id) from StaffAccount c")
    String lastId();
}
