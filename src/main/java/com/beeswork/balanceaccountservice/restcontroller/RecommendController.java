package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.account.AccountProfileDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.service.recommend.RecommendService;
import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/recommend")
public class RecommendController extends BaseController {


    private final RecommendService recommendService;

    @Autowired
    public RecommendController(ObjectMapper objectMapper, ModelMapper modelMapper, RecommendService recommendService) {
        super(objectMapper, modelMapper);
        this.recommendService = recommendService;
    }

    @GetMapping("/accounts")
    public ResponseEntity<String> recommendAccounts(@RequestParam("account_id") @ValidUUID String accountId,
                                                    @RequestParam("distance") @Min(1000) @Max(10000) int distance,
                                                    @RequestParam("min_age") int minAge,
                                                    @RequestParam("max_age") int maxAge,
                                                    @RequestParam("gender") boolean gender,
                                                    @RequestParam double latitude,
                                                    @RequestParam double longitude)
    throws AccountNotFoundException, JsonProcessingException {

        List<AccountProfileDTO> accountProfileDTOs = recommendService.accountsWithin(accountId, distance, minAge, maxAge, gender, latitude, longitude);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(accountProfileDTOs));
    }

    @PostMapping("/swipe")
    public ResponseEntity<String> swipeAccount(@RequestParam("swiper_id") @ValidUUID String swiperId,
                                               @RequestParam("swiper_email") String swiperEmail,
                                               @RequestParam("swiped_id") @ValidUUID String swipedId)
    throws AccountNotFoundException {

        long swipeId = recommendService.swipe(swiperId, swiperEmail, swipedId);
        return ResponseEntity.status(HttpStatus.OK).body(String.valueOf(swipeId));
    }


}
