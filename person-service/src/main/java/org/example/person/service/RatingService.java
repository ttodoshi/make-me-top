package org.example.person.service;

import java.util.List;
import java.util.Map;

public interface RatingService {
    Double getPersonRatingAsKeeper(Integer personId);

    Double getPersonRatingAsExplorer(Integer personId);

    Map<Integer, Double> getPeopleRatingAsExplorerByPersonIdIn(List<Integer> personIds);

    Map<Integer, Double> getPeopleRatingAsKeeperByPersonIdIn(List<Integer> personIds);
}
