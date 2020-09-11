package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.service.recommend.RecommendService;
import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    throws AccountNotFoundException {

        recommendService.accountsWithin(UUID.fromString(accountId), distance, minAge, maxAge, gender, latitude, longitude);

        return ResponseEntity.status(HttpStatus.OK).body("");
    }


}
