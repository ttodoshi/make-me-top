package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.Person;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.PersonRepository;
import org.example.repository.course.CourseRepository;
import org.example.repository.feedback.ExplorerFeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeeperPublicInformationService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRepository courseRepository;
    private final PersonRepository personRepository;
    private final ExplorerFeedbackRepository explorerFeedbackRepository;

    private final RatingService ratingService;

    @Transactional(readOnly = true)
    public Map<String, Object> getKeeperPublicInformation(Integer personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("person", person);
        response.put("rating", ratingService.getKeeperRating(personId));
        response.put("totalSystems", keeperRepository.getKeeperSystemsCount(personId));
        response.put("totalExplorers", explorerRepository.getExplorersCountForKeeper(personId));
        response.put("systems", courseRepository.findCoursesByKeeperPersonId(personId));
        response.put("feedback", explorerFeedbackRepository.getKeeperCommentsByPersonId(personId));
        return response;
    }
}
