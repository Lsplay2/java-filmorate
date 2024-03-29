package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    public ReviewStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Review create(Review review) throws NotFoundException {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select FILM_ID from FILM where FILM_ID = ?",
                review.getFilmId());
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select USER_ID from USERS where USER_ID = ?",
                review.getUserId());
        if (filmRows.next() && userRows.next()) {
            String sqlQuery = "insert into REVIEW(FILM_ID, IS_POSITIVE, CONTENT, AUTHOR_ID) " +
                    "values (?, ?, ?, ?)";

            jdbcTemplate.update(sqlQuery,
                    review.getFilmId(),
                    review.getIsPositive(),
                    review.getContent(),
                    review.getUserId()
            );
            SqlRowSet sqlRows = jdbcTemplate.queryForRowSet("select count(REVIEW_ID) as last_id from REVIEW");
            sqlRows.next();
            review.setReviewId(sqlRows.getInt("last_id"));
            log.info("CREATE " + review);
            return review;
        } else {
            throw new NotFoundException("нет такого фильма");
        }
    }

    public Optional<Review> update(Review review) throws NotFoundException {
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("select REVIEW_ID from REVIEW where REVIEW_ID = ?",
                review.getReviewId());
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select FILM_ID from FILM where FILM_ID = ?",
                review.getFilmId());
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select USER_ID from USERS where USER_ID = ?",
                review.getUserId());
        if (reviewRows.next() && filmRows.next() && userRows.next()) {
            String sqlQuery = "update REVIEW set " +
                    "IS_POSITIVE = ?, CONTENT = ? " +
                    "where REVIEW_ID = ?";
            jdbcTemplate.update(sqlQuery,
                    review.getIsPositive(),
                    review.getContent(),
                    review.getReviewId()
            );
            log.info("UPDATE " + getReview(review.getReviewId()));
            return Optional.ofNullable(getReview(review.getReviewId()));
        } else {
            throw new NotFoundException("мертвое умереть не может");
        }
    }

    public void delete(Integer reviewId) throws NotFoundException {
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("select REVIEW_ID from REVIEW where REVIEW_ID = ?",
                reviewId);
        if (reviewRows.next()) {
            String sqlQuery = "delete from REVIEW where REVIEW_ID = ?";

            jdbcTemplate.update(sqlQuery,
                    reviewId
            );
            log.info("DELETE " + reviewId);
        } else {
            throw new NotFoundException("мертвое умереть не может");
        }
    }

    public void addMark(Integer reviewId, Integer userId, Boolean isLike) throws Exception {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select USER_ID from USERS where USER_ID = ?",
                userId);
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("select REVIEW_ID from REVIEW where REVIEW_ID = ?",
                reviewId);
        Boolean isExistUser = userRows.next();
        Boolean isExistReview = reviewRows.next();
        if (isExistUser && isExistReview) {
            SqlRowSet fullRows = jdbcTemplate.queryForRowSet("select USER_ID from REVIEW_LIKES where REVIEW_ID = ?" +
                            " and USER_ID = ?", reviewId,
                    userId);
            if (fullRows.next()) {
                throw new Exception("Одна команда - один капитан! Один пользователь - один лайк или дизлайк!");
            } else {
                String sqlQuery = "insert into REVIEW_LIKES(REVIEW_ID, USER_ID, IS_LIKE) " +
                        "values (?, ?, ?)";

                jdbcTemplate.update(sqlQuery,
                        reviewId,
                        userId,
                        isLike
                );
            }
        } else if (isExistUser) {
            throw new NotFoundException("нет такого ревью");
        } else {
            throw new NotFoundException("нет такого пользователя");
        }
    }

    public void deleteMark(Integer reviewId, Integer userId, Boolean isLike) throws Exception {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select USER_ID from USERS where USER_ID = ?",
                userId);
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("select REVIEW_ID from REVIEW where REVIEW_ID = ?",
                reviewId);
        Boolean isExistUser = userRows.next();
        Boolean isExistReview = reviewRows.next();
        if (isExistUser && isExistReview) {
            SqlRowSet fullRows = jdbcTemplate.queryForRowSet("select USER_ID from REVIEW_LIKES where REVIEW_ID = ?" +
                            " and USER_ID = ?", reviewId,
                    userId);
            if (fullRows.next()) {
                throw new Exception("Одна команда - один капитан! Один пользователь - один лайк или дизлайк!");
            } else {
                String sqlQuery = "delete from REVIEW_LIKES where REVIEW_ID = ? and USER_ID = ? and IS_LIKE = ?";

                jdbcTemplate.update(sqlQuery,
                        reviewId,
                        userId,
                        isLike
                );
            }
        } else if (isExistUser) {
            throw new NotFoundException("нет такого ревью");
        } else {
            throw new NotFoundException("нет такого пользователя");
        }
    }

    public Review getReview(Integer reviewId) throws NotFoundException {
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("select * from REVIEW where REVIEW_ID = ?",
                reviewId);
        if (reviewRows.next()) {
            Review review = Review.builder().reviewId(reviewId)
                    .content(reviewRows.getString("CONTENT"))
                    .filmId(reviewRows.getInt("FILM_ID"))
                    .isPositive(reviewRows.getBoolean("IS_POSITIVE"))
                    .userId(reviewRows.getInt("AUTHOR_ID"))
                    .build();
            setUseful(review);
            log.info("GET REVIEW " + review);
            return review;
        } else {
            throw new NotFoundException("а возвращать-то и нечего");
        }
    }

    public List<Review> getReviews(Integer filmId) {
        String sqlReview;
        List<Review> reviews;
        if (filmId != null) {
            sqlReview = "select * from REVIEW where FILM_ID = ?";
            reviews = jdbcTemplate.query(sqlReview, (reviewRows, rowNum) -> Review.builder()
                    .reviewId(reviewRows.getInt("REVIEW_ID"))
                    .content(reviewRows.getString("CONTENT"))
                    .filmId(reviewRows.getInt("FILM_ID"))
                    .isPositive(reviewRows.getBoolean("IS_POSITIVE"))
                    .userId(reviewRows.getInt("AUTHOR_ID"))
                    .build(), filmId
            );
        } else {
            sqlReview = "select * from REVIEW";
            reviews = jdbcTemplate.query(sqlReview, (reviewRows, rowNum) -> Review.builder()
                    .reviewId(reviewRows.getInt("REVIEW_ID"))
                    .content(reviewRows.getString("CONTENT"))
                    .filmId(reviewRows.getInt("FILM_ID"))
                    .isPositive(reviewRows.getBoolean("IS_POSITIVE"))
                    .userId(reviewRows.getInt("AUTHOR_ID"))
                    .build()
            );
        }
        log.info("GET REVIEWS " + reviews);
        reviews.forEach(review -> setUseful(review));
        log.info("GET REVIEWS " + reviews);
        return reviews;
    }

    private void setUseful(Review review) {
        log.info("USEFUL " + review);
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select count(*) as LIKES from REVIEW_LIKES " +
                        "where REVIEW_ID = ? and IS_LIKE = ?",
                review.getReviewId(), true);
        likesRows.next();
        SqlRowSet dislikesRows = jdbcTemplate.queryForRowSet("select count(*) as DISLIKES from REVIEW_LIKES " +
                        "where REVIEW_ID = ? and IS_LIKE = ?",
                review.getReviewId(), false);
        dislikesRows.next();
        review.setUseful(likesRows.getInt("LIKES") - dislikesRows.getInt("DISLIKES"));
        log.info("USEFUL " + review);
    }
}
