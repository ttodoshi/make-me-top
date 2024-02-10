package org.example.feedback.service;

import java.util.List;
import java.util.Map;

public interface RatingService {
    Double getPersonRatingAsExplorer(Long personId);

    Double getPersonRatingAsKeeper(Long personId);

    Map<Long, Double> getPeopleRatingAsExplorer(List<Long> personIds);

    Map<Long, Double> getPeopleRatingAsKeeper(List<Long> personIds);
}
