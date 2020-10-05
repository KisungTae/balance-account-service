package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.service.question.QuestionService;
import com.beeswork.balanceaccountservice.util.Convert;
import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class QuestionController extends BaseController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(ObjectMapper objectMapper, ModelMapper modelMapper, QuestionService questionService) {
        super(objectMapper, modelMapper);
        this.questionService = questionService;
    }

}
