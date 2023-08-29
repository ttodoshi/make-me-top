package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.repository.PersonRepository;
import org.example.repository.feedback.ExplorerFeedbackRepository;
import org.example.repository.feedback.KeeperFeedbackRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final PersonRepository personRepository;
    private final KeeperFeedbackRepository keeperFeedbackRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;

    public Double getKeeperRating(Integer personId) {
        if (!personRepository.existsById(personId))
            throw new PersonNotFoundException(personId);
        return Math.ceil(explorerFeedbackRepository.getKeeperRating(personId).orElse(0.0) * 10) / 10;
    }

    public Double getExplorerRating(Integer personId) {
        if (!personRepository.existsById(personId))
            throw new PersonNotFoundException(personId);
        return Math.ceil(keeperFeedbackRepository.getExplorerRating(personId).orElse(0.0) * 10) / 10;
    }
}
