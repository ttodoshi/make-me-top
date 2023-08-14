package org.example.repository;

public interface RatingRepository {
    Double getExplorerRating(Integer personId);

    Double getKeeperRating(Integer personId);
}
