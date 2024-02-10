package org.example.feedback.exception.feedback;

public class OfferNotFoundException extends RuntimeException {
    public OfferNotFoundException(Long offerId) {
        super("Предложение оставить отзыв " + offerId + " не найдено");
    }
}
