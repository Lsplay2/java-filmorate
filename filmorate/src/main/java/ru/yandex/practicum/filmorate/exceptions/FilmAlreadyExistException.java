package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FilmAlreadyExistException extends Exception{
    public FilmAlreadyExistException (String message){
        super(message);
    }
}
