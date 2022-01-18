package com.dso34bt.jobportal.repositories;

import com.dso34bt.jobportal.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query("SELECT new Document(d.id, d.name, d.title, d.size) FROM Document d WHERE d.candidateAccount.email = ?1")
    List<Document> findCandidateDocuments(String email);

    Optional<Document> findByCandidateAccountEmailAndTitle(String email, String title);

    boolean existsByCandidateAccountEmailAndTitle(String email, String title);

    boolean existsByCandidateAccountEmail(String email);

    @Query("select max(c.id) from Document c")
    String lastId();
}
