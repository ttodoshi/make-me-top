package org.example.feedback.exception.classes.feedback;

public class OfferAlreadyNotValidException extends RuntimeException {
    public OfferAlreadyNotValidException(Long offerId) {
        super("Предложение на оставление отзыва " + offerId + " уже закрыто");
    }
}
