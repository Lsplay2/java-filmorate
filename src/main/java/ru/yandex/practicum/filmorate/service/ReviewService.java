package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {

    @Autowired
    private ReviewStorage reviewStorage;
    @Autowired
    private EventService eventService;

    public Review create(Review review) throws NotFoundException, ValidationException {
        validateReview(review);
        Review reviewCreated = reviewStorage.create(review);
        eventService.createEvent(reviewCreated.getUserId(), EventType.REVIEW, EventOperation.ADD, reviewCreated.getReviewId());
        return reviewCreated;
    }

    public Review update(Review review) throws NotFoundException, ValidationException {
        validateReview(review);
        Optional<Review> updatedReview = reviewStorage.update(review);

        if (updatedReview.isPresent()) {
            Review reviewUpdated = updatedReview.get();
            eventService.createEvent(reviewUpdated.getUserId(), EventType.REVIEW, EventOperation.UPDATE, reviewUpdated.getReviewId());
            return reviewUpdated;
        } else {
            throw new NotFoundException("Отзыв не найден.");
        }
    }

    public void delete(Integer reviewId) throws NotFoundException {
        Review review = getReview(reviewId);
        reviewStorage.delete(reviewId);
        eventService.createEvent(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, review.getReviewId());
    }

    public void addMark(Integer reviewId, Integer userId, Boolean isLike) throws Exception {
        reviewStorage.addMark(reviewId, userId, isLike);
    }

    public void deleteMark(Integer reviewId, Integer userId, Boolean isLike) throws Exception {
        reviewStorage.deleteMark(reviewId, userId, isLike);
    }

    public List<Review> getTopReviews(Integer filmId, Integer count) throws NotFoundException {
        return reviewStorage.getReviews(filmId).stream()
                .sorted((r0, r1) -> r1.getUseful() - r0.getUseful())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Review getReview(Integer reviewId) throws NotFoundException {
        return reviewStorage.getReview(reviewId);
    }

    private void validateReview(Review review) throws ValidationException {
        if (review.getIsPositive() == null) {
            throw new ValidationException("Должна быть оценка");
        } else if (review.getUserId() == null) {
            throw new ValidationException("проблема в userId");
        } else if (review.getContent() == null) {
            throw new ValidationException("может стоит что-нибудь написать?");
        } else if (review.getFilmId() == null) {
            throw new ValidationException("на что обзор?");
        }
    }
}
