package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.account.CardDTO;
import com.beeswork.balanceaccountservice.dto.recommend.RecommendDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.service.recommend.RecommendService;
import com.beeswork.balanceaccountservice.vm.recommend.RecommendVM;
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
@RequestMapping("/recommend")
public class RecommendController extends BaseController {

    private final RecommendService recommendService;

    @Autowired
    public RecommendController(ObjectMapper objectMapper, ModelMapper modelMapper, RecommendService recommendService) {
        super(objectMapper, modelMapper);
        this.recommendService = recommendService;
    }

    @GetMapping
    public ResponseEntity<String> recommend(@Valid @ModelAttribute RecommendVM recommendVM,
                                            BindingResult bindingResult)
    throws AccountNotFoundException, JsonProcessingException {

        if (bindingResult.hasErrors()) throw new BadRequestException();

        List<CardDTO> cardDTOs = recommendService.recommend(modelMapper.map(recommendVM, RecommendDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(cardDTOs));
    }


}
