package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.account.AccountProfileDTO;
import com.beeswork.balanceaccountservice.dto.match.SwipeAddedDTO;
import com.beeswork.balanceaccountservice.dto.match.SwipeDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountInvalidException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.account.AccountShortOfPointException;
import com.beeswork.balanceaccountservice.exception.swipe.SwipeBalancedExistsException;
import com.beeswork.balanceaccountservice.service.match.MatchService;
import com.beeswork.balanceaccountservice.util.Convert;
import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.match.SwipeVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping("/match")
public class MatchController extends BaseController {


    private final MatchService matchService;

    @Autowired
    public MatchController(ObjectMapper objectMapper, ModelMapper modelMapper, MatchService matchService) {
        super(objectMapper, modelMapper);
        this.matchService = matchService;
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

        List<AccountProfileDTO> accountProfileDTOs = matchService.recommend(accountId, distance, minAge, maxAge, gender, latitude, longitude);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(accountProfileDTOs));
    }

    @PostMapping("/swipe")
    public ResponseEntity<String> swipeAccount(@Valid @RequestBody SwipeVM swipeVM,
                                               BindingResult bindingResult)
    throws AccountNotFoundException, AccountInvalidException, SwipeBalancedExistsException, JsonProcessingException,
           AccountShortOfPointException {

        SwipeAddedDTO swipeAddedDTO = matchService.swipe(modelMapper.map(swipeVM, SwipeDTO.class));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(swipeAddedDTO));
    }


}
