package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.Document;
import com.dso34bt.jobportal.repositories.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    private final DocumentRepository repository;

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    public List<Document> getCandidateDocuments(String email){
        return repository.findCandidateDocuments(email);
    }

    public List<Document> findAll(){
        return repository.findAll();
    }

    public boolean saveDocument(Document document){
        repository.save(document);

        return repository.existsByCandidateAccountEmailAndTitle(document.getCandidateAccount().getEmail(), document.getTitle());
    }

    public boolean existsByCandidateEmailAndTitle(String email, String title){
        return repository.existsByCandidateAccountEmailAndTitle(email, title);
    }

    public boolean existsByCandidateEmail(String email){
        return repository.existsByCandidateAccountEmail(email);
    }

    public Optional<Document> findByCandidateEmailAndTitle(String email, String title){
        return repository.findByCandidateAccountEmailAndTitle(email, title);
    }

    public Optional<Document> findById(long id){
        return repository.findById(id);
    }

    public boolean deleteByEntity(Document document){
        repository.delete(document);

        return repository.existsById(document.getId());
    }

    public long getLastId(){
        if (repository.lastId() == null)
            return 0;

        return Long.parseLong(repository.lastId());
    }
}
