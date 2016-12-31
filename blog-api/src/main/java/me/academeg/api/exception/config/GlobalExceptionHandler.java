package me.academeg.api.exception.config;

import me.academeg.api.common.ApiResult;
import me.academeg.api.common.ApiResultImpl;
import me.academeg.api.common.ApiResultWithData;
import me.academeg.api.common.CollectionResult;
import me.academeg.api.exception.EntityExistException;
import me.academeg.api.exception.EntityNotExistException;
import me.academeg.api.exception.entity.AccountPermissionException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

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
    public ApiResult handle(OAuth2Exception ex) {
        return new ApiResultImpl(1010, ex.getMessage());
    }

    @ExceptionHandler
    public ApiResult handle(AccountPermissionException ex) {
        return new ApiResultImpl(2000, ex.getMessage());
    }

    @ExceptionHandler
    public ApiResult handle(EntityExistException ex) {
        return new ApiResultImpl(3000, ex.getMessage());
    }

    @ExceptionHandler
    public ApiResult handle(EntityNotExistException ex) {
        return new ApiResultImpl(4000, ex.getMessage());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult handle(MethodArgumentNotValidException ex) {
        return new ApiResultWithData(
            4000,
            "Argument not valid",
            new CollectionResult<>(
                ex
                    .getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList()))
        );
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult handle(ConstraintViolationException ex) {
        return new ApiResultWithData(
            4010,
            "Constraint exception",
            new CollectionResult<>(
                ex.
                    getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList()))
        );
    }

//    @ExceptionHandler
//    @ResponseBody
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ApiResult handle(Exception ex) {
//        return new ApiResultImpl(6000, "Internal Server Exception");
//    }
}
