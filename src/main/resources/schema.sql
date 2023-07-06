DROP TABLE IF EXISTS USER_FILM CASCADE;
DROP TABLE IF EXISTS USER_FRIEND CASCADE;
DROP TABLE IF EXISTS GENRE_FILM CASCADE;
DROP TABLE IF EXISTS REVIEW_LIKES CASCADE;
DROP TABLE IF EXISTS REVIEW CASCADE;
DROP TABLE IF EXISTS FILM CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS GENRE CASCADE;
DROP TABLE IF EXISTS RATING CASCADE;



CREATE TABLE IF NOT EXISTS "RATING" (
	"RATING_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"NAME" VARCHAR_IGNORECASE(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS "FILM" (
	"FILM_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"RATING_ID" INTEGER,
	"RELEASEDATE" TIMESTAMP NOT NULL,
	"DURATION" INTEGER NOT NULL,
	"NAME" VARCHAR_IGNORECASE(100) NOT NULL,
	"DESCRIPTION" VARCHAR_IGNORECASE(250) NOT NULL,
	CONSTRAINT film_fk_rating FOREIGN KEY ("RATING_ID") REFERENCES RATING("RATING_ID")
);



CREATE TABLE IF NOT EXISTS "GENRE" (
	"GENRE_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"NAME" VARCHAR_IGNORECASE NOT NULL
);

CREATE TABLE IF NOT EXISTS "GENRE_FILM" (
	"GENRE_FILM_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"GENRE_ID" INTEGER NOT NULL,
	"FILM_ID" INTEGER NOT NULL,
	CONSTRAINT fk_genre FOREIGN KEY ("GENRE_ID") REFERENCES GENRE("GENRE_ID"),
	CONSTRAINT fk_film FOREIGN KEY ("FILM_ID") REFERENCES FILM("FILM_ID")
);


CREATE TABLE IF NOT EXISTS "USERS" (
	"USER_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"NAME" VARCHAR_IGNORECASE,
	"EMAIL" VARCHAR_IGNORECASE NOT NULL,
	"LOGIN" VARCHAR_IGNORECASE NOT NULL,
	"BIRTHDAY" TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS "USER_FILM" (
	"USER_FILM_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"USER_ID" INTEGER,
	"FILM_ID" INTEGER,
	CONSTRAINT fk_user FOREIGN KEY ("USER_ID") REFERENCES USERS("USER_ID"),
	CONSTRAINT fk_film2 FOREIGN KEY ("FILM_ID") REFERENCES FILM("FILM_ID")
);

CREATE TABLE IF NOT EXISTS "USER_FRIEND" (
	"USER_FRIEND_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"USER_ID" INTEGER,
	"FRIEND_ID" INTEGER,
	"CONFIRM" BOOLEAN,
	CONSTRAINT fk_user2 FOREIGN KEY ("USER_ID") REFERENCES USERS("USER_ID"),
	CONSTRAINT fk_friend FOREIGN KEY ("FRIEND_ID") REFERENCES USERS("USER_ID")
);

CREATE TABLE IF NOT EXISTS "REVIEW" (
    "REVIEW_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	"FILM_ID" INTEGER,
	"IS_POSITIVE" BOOLEAN NOT NULL,
    "CONTENT" VARCHAR_IGNORECASE NOT NULL,
    "AUTHOR_ID" INTEGER,
	CONSTRAINT fk_film_review_1 FOREIGN KEY ("FILM_ID") REFERENCES FILM("FILM_ID"),
	CONSTRAINT fk_author_review_1 FOREIGN KEY ("AUTHOR_ID") REFERENCES USERS("USER_ID")
);

CREATE TABLE IF NOT EXISTS "REVIEW_LIKES" (
    "REVIEW_ID" INTEGER,
    "USER_ID" INTEGER,
    "IS_LIKE" BOOLEAN,
    CONSTRAINT fk_review_1 FOREIGN KEY ("REVIEW_ID") REFERENCES REVIEW("REVIEW_ID"),
    CONSTRAINT fk_user_review_1 FOREIGN KEY ("USER_ID") REFERENCES USERS("USER_ID")
);

ALTER TABLE "USER_FILM" ADD CONSTRAINT IF NOT EXISTS "USER_FILM_FK" FOREIGN KEY (FILM_ID) REFERENCES "FILM"("FILM_ID")  ON DELETE SET NULL ON UPDATE SET NULL;
ALTER TABLE "USER_FILM" ADD CONSTRAINT IF NOT EXISTS "USER_FILM_FK_1" FOREIGN KEY (USER_ID) REFERENCES "USERS"("USER_ID")  ON DELETE SET NULL ON UPDATE SET NULL;
ALTER TABLE "GENRE_FILM" ADD CONSTRAINT IF NOT EXISTS "GENRE_FK" FOREIGN KEY (GENRE_ID) REFERENCES "GENRE"("GENRE_ID") ON DELETE SET NULL ON UPDATE SET NULL;
ALTER TABLE "GENRE_FILM" ADD CONSTRAINT IF NOT EXISTS "FILM_FK_GENRE" FOREIGN KEY (FILM_ID) REFERENCES "FILM"("FILM_ID") ON DELETE SET NULL ON UPDATE SET NULL;
ALTER TABLE "USER_FRIEND" ADD CONSTRAINT IF NOT EXISTS "USER_FK" FOREIGN KEY (USER_ID) REFERENCES "USERS"("USER_ID") ON DELETE SET NULL ON UPDATE SET NULL;
ALTER TABLE "USER_FRIEND" ADD CONSTRAINT IF NOT EXISTS "FRIEND_FK" FOREIGN KEY (FRIEND_ID) REFERENCES "USERS"("USER_ID") ON DELETE SET NULL ON UPDATE SET NULL;
ALTER TABLE "FILM" ADD CONSTRAINT IF NOT EXISTS "FILM_FK" FOREIGN KEY ("RATING_ID") REFERENCES "RATING"("RATING_ID") ON DELETE SET NULL ON UPDATE SET NULL;
ALTER TABLE "REVIEW_LIKES" ADD CONSTRAINT IF NOT EXISTS "REVIEW_USER_FK_2" FOREIGN KEY (USER_ID) REFERENCES "USERS"("USER_ID")  ON DELETE CASCADE ON UPDATE SET NULL;
ALTER TABLE "REVIEW_LIKES" ADD CONSTRAINT IF NOT EXISTS "USER_REVIEW_FK_2" FOREIGN KEY (REVIEW_ID) REFERENCES "REVIEW"("REVIEW_ID")  ON DELETE CASCADE ON UPDATE SET NULL;
ALTER TABLE "REVIEW" ADD CONSTRAINT IF NOT EXISTS "REVIEW_USER_FK_3" FOREIGN KEY (AUTHOR_ID) REFERENCES "USERS"("USER_ID")  ON DELETE CASCADE ON UPDATE SET NULL;
ALTER TABLE "REVIEW" ADD CONSTRAINT IF NOT EXISTS "REVIEW_FILM_FK_3" FOREIGN KEY (FILM_ID) REFERENCES "FILM"("FILM_ID")  ON DELETE CASCADE ON UPDATE SET NULL;