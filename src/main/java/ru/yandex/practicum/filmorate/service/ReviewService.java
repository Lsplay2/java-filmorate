package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {

    @Autowired
    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review create(Review review) throws NotFoundException, ValidationException {
        validateReview(review);
        return reviewStorage.create(review);
    }

    public Review update(Review review) throws NotFoundException, ValidationException {
        validateReview(review);
        return reviewStorage.update(review);
    }

    public void delete(Integer reviewId) throws NotFoundException {
        reviewStorage.delete(reviewId);
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
