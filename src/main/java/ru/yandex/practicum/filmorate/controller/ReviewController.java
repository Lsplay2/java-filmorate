package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {

    @Autowired
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review create(@RequestBody Review review) throws NotFoundException, ValidationException {
        log.info("Поступил запрос на создание ревью:" + review);
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@RequestBody Review review) throws NotFoundException, ValidationException {
        log.info("Поступил запрос на обновление ревью:" + review);
        return reviewService.update(review);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteReview(@PathVariable int id) throws NotFoundException {
        log.info("Поступил запрос на удаление ревью:" + id);
        reviewService.delete(id);
    }

    @GetMapping(value = "/{id}")
    public Review getReview(@PathVariable int id) throws NotFoundException {
        log.info("Поступил запрос на получение ревью по id:" + id);
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> getTopReviews(@RequestParam(required = false) Integer filmId,
                                      @RequestParam(defaultValue = "10") Integer count) throws NotFoundException {
        log.info("Поступил запрос на получение топ ревью");
        log.info("filmId = {}, count = {}", filmId, count);
        return reviewService.getTopReviews(filmId, count);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void likeReview(@PathVariable int id, @PathVariable int userId) throws Exception {
        reviewService.addMark(id, userId, true);
    }

    @PutMapping(value = "/{id}/dislike/{userId}")
    public void dislikeReview(@PathVariable int id, @PathVariable int userId) throws Exception {
        reviewService.addMark(id, userId, false);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLikeReview(@PathVariable int id, @PathVariable int userId) throws Exception {
        reviewService.deleteMark(id, userId, true);
    }

    @DeleteMapping(value = "/{id}/dislike/{userId}")
    public void deleteDislikeReview(@PathVariable int id, @PathVariable int userId) throws Exception {
        reviewService.deleteMark(id, userId, false);
    }
}
