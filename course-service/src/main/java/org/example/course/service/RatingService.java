package org.example.course.service;

import java.util.List;
import java.util.Map;

public interface RatingService {
    Map<Long, Double> getPeopleRatingAsKeeperByPersonIdIn(String authorizationHeader, List<Long> personIds);

    Map<Long, Double> getPeopleRatingAsExplorerByPersonIdIn(String authorizationHeader, List<Long> personIds);
}
