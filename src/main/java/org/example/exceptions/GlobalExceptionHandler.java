package org.example.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    public ModelAndView handleLocationNotFoundException(LocationNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("search-results");
        modelAndView.addObject("errorMessage", e.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(Exception e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "An unexpected error occurred.");
        return modelAndView;
    }
}
