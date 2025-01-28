package org.example.exceptions;

import lombok.extern.log4j.Log4j2;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    public String handleLocationNotFoundException(LocationNotFoundException e, Model model) {

        model.addAttribute("error", e.getMessage());
        return "search-results";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);

        return "error";
    }
}