package org.example.service;

public interface RatingService {
    Double getExplorerRating(Integer personId);

    Double getKeeperRating(Integer personId);
}
