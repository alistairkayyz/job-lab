package com.dso34bt.jobportal.services;

import com.dso34bt.jobportal.model.AssessmentResults;
import com.dso34bt.jobportal.repositories.AssessmentResultsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssessmentResultsService {
    private final AssessmentResultsRepository repository;

    public AssessmentResultsService(AssessmentResultsRepository repository) {
        this.repository = repository;
    }

    public Optional<AssessmentResults> getCandidateResults(long candidateId, long assessmentId){
        return repository.findByCandidateIdAndJobPostAssessmentId(candidateId, assessmentId);
    }

    public List<AssessmentResults> getByAssessmentId(long id){
        return repository.findByJobPostAssessmentId(id);
    }

    public boolean saveAssessmentResults(AssessmentResults assessmentResults){
        repository.save(assessmentResults);

        return repository.existsByCandidate_Id(assessmentResults.getCandidate().getId());
    }

    public boolean deleteById(AssessmentResults assessmentResults){
        repository.deleteById(assessmentResults.getId());

        return repository.existsByCandidate_Id(assessmentResults.getCandidate().getId());
    }

    public long getLastId(){
        if (repository.lastId() == null)
            return 0;
        return Long.parseLong(repository.lastId());
    }
}
