package com.beeswork.balanceaccountservice.controlleradvice;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice(annotations = RestController.class)
public class ControllerAdvice {

    @InitBinder
    public void initBinder (WebDataBinder binder ) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringTrimmerEditor);
    }
}
