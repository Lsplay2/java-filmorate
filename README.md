# java-filmorate
Template repository for Filmorate project.
Schema BD https://lucid.app/lucidchart/931d7c20-658f-453e-bff5-d56a67da66b9/edit?viewport_loc=908%2C287%2C1583%2C852%2C0_0&invitationId=inv_52fe448e-f6fb-43c2-a3a2-9f467fa6ede6

1)Таблица genre и rating относятся к таблице film Один ко многим (Один рейтинг/жанр на несколько фильмов) При взятии фильма через джоин будет указываться жанр и рейтинг из других таблиц.
Primary key genre -> genre_id | rating -> rating_id. Foreign key в таблице film genre -> genre_id | rating -> rating_id

2)Таблица users относится к сама к себе primary key -> user_id  для поиска друзей среди других пользователей. Отношение Многие ко многим (У одного пользователя много друзей у друга может быть много друзей)

3)Таблицы users и films относятся друг к другу при помощи отношения Многие ко многим (У фильма лайки от нескольких пользователей и у пользователя поставлены лайки нескольким фильмам) 


