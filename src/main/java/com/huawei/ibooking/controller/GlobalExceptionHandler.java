package com.huawei.ibooking.controller;

import com.huawei.ibooking.model.MyResponseBody;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public MyResponseBody exceptionHandler(Exception e) {
        return new MyResponseBody("500", e.getClass().getSimpleName(), e.getMessage());
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public MyResponseBody constraintHandler(ConstraintViolationException e) {
        return new MyResponseBody("500", "参数错误", e.getMessage());
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public MyResponseBody methodArgumentHandler(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        List<String> list = new ArrayList<>();
        for (ObjectError error : allErrors) {
            if(error instanceof FieldError)
                list.add(((FieldError) error).getField() + error.getDefaultMessage());
            else
                list.add(error.getDefaultMessage());
        }
        return new MyResponseBody("500", "参数错误", list.get(0));
    }
}
