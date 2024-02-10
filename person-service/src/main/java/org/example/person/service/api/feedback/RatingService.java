package org.example.person.service.api.feedback;

import java.util.List;
import java.util.Map;

public interface RatingService {
    Double getPersonRatingAsKeeper(Long personId);

    Double getPersonRatingAsExplorer(Long personId);

    Map<Long, Double> getPeopleRatingAsExplorerByPersonIdIn(List<Long> personIds);

    Map<Long, Double> getPeopleRatingAsKeeperByPersonIdIn(List<Long> personIds);
}
