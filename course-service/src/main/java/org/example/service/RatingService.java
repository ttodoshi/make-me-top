package org.example.service;

import java.util.List;
import java.util.Map;

public interface RatingService {
    Map<Integer, Double> getPeopleRatingAsKeeperByPersonIdIn(List<Integer> personIds);

    Map<Integer, Double> getPeopleRatingAsExplorerByPersonIdIn(List<Integer> personIds);
}
