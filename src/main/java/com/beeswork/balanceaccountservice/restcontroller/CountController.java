package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.dto.count.CountTabDTO;
import com.beeswork.balanceaccountservice.service.count.CountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/count")
public class CountController extends BaseController {

    private final CountService countService;

    @Autowired
    public CountController(CountService countService, ObjectMapper objectMapper, ModelMapper modelMapper) {
        super(objectMapper, modelMapper);
        this.countService = countService;
    }

    @GetMapping("/tabs")
    public ResponseEntity<String> countTabs(Principal principal) throws JsonProcessingException {
        List<CountTabDTO> countTabDTOs = countService.countTabs(getAccountIdFrom(principal));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(countTabDTOs));
    }
}
