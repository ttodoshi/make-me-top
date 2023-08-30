package org.example.service;

public interface RatingService {
    Double getKeeperRating(Integer personId);

    Double getExplorerRating(Integer personId);
}
