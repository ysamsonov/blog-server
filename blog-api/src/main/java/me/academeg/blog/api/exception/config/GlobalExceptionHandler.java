package me.academeg.blog.api.exception.config;

import me.academeg.blog.api.common.*;
import me.academeg.blog.api.exception.BlogEntityExistException;
import me.academeg.blog.api.exception.BlogEntityNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler Configuration
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult handle(OAuth2Exception ex) {
        return new ApiResultImpl(1010, ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult handle(BlogEntityExistException ex) {
        return new ApiResultImpl(3000, ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult handle(BlogEntityNotExistException ex) {
        return new ApiResultImpl(4000, ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult handle(MethodArgumentNotValidException ex) {
        return new ApiResultWithData<>(
            4010,
            "Argument not valid",
            new MapResult<>(
                ex
                    .getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .collect(
                        Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                        )
                    )
            )
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult handle(ConstraintViolationException ex) {
        return new ApiResultWithData<>(
            4020,
            "Constraint exception",
            new CollectionResult<>(
                ex.
                    getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList())
            )
        );
    }

//    @ExceptionHandler
//    @ResponseBody
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ApiResult handle(Exception ex) {
//        return new ApiResultImpl(6000, "Internal Server Exception");
//    }
}
