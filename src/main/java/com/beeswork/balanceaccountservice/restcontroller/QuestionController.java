package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.service.question.QuestionService;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import com.beeswork.balanceaccountservice.vm.question.RandomQuestionVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController extends BaseController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(ObjectMapper objectMapper, ModelMapper modelMapper, QuestionService questionService) {
        super(objectMapper, modelMapper);
        this.questionService = questionService;
    }

    @GetMapping("/list")
    public ResponseEntity<String> listQuestions(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
                                                BindingResult bindingResult) throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        List<QuestionDTO> questionDTOs = questionService.listQuestions(accountIdentityVM.getAccountId(),
                                                                       accountIdentityVM.getIdentityToken());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionDTOs));
    }

    @GetMapping("/random")
    public ResponseEntity<String> randomQuestion(@Valid @ModelAttribute RandomQuestionVM randomQuestionVM,
                                                 BindingResult bindingResult)
    throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        QuestionDTO questionDTO = questionService.randomQuestion(randomQuestionVM.getCurrentQuestionIds());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionDTO));
    }

    @GetMapping("/random/list")
    public ResponseEntity<String> listRandomQuestions(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
                                                      BindingResult bindingResult) throws JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();
        List<QuestionDTO> questionDTOs = questionService.listRandomQuestions(accountIdentityVM.getAccountId(),
                                                                             accountIdentityVM.getIdentityToken());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionDTOs));

    }


}
