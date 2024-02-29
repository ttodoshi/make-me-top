package org.example.person.service.implementations.profile;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.person.GetPersonDto;
import org.example.person.dto.profile.PersonProfileDto;
import org.example.person.service.api.course.CourseService;
import org.example.person.service.api.feedback.FeedbackService;
import org.example.person.service.api.feedback.RatingService;
import org.example.person.service.implementations.PersonService;
import org.modelmapper.ModelMapper;

@RequiredArgsConstructor
public abstract class PersonProfileInformationService {
    protected final RatingService ratingService;
    protected final CourseService courseService;
    protected final FeedbackService feedbackService;
    private final PersonService personService;
    private final ModelMapper mapper;

    protected <T extends PersonProfileDto> T addPersonProfileInformation(T personInformation, Long personId, Integer totalSystems) {
        personInformation.setPerson(
                mapper.map(
                        personService.findPersonById(personId),
                        GetPersonDto.class
                )
        );
        personInformation.setTotalSystems(totalSystems);
        return personInformation;
    }
}
