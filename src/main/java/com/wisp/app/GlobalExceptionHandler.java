package com.wisp.app;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NegocioException.class)
    public ModelAndView negocio(NegocioException ex) {
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }
}