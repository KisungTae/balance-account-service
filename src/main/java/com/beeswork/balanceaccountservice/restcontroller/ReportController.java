package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.report.ReportService;
import com.beeswork.balanceaccountservice.vm.report.ReportVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/report")
public class ReportController extends BaseController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ObjectMapper objectMapper,
                            ModelMapper modelMapper,
                            ReportService reportService) {
        super(objectMapper, modelMapper);
        this.reportService = reportService;
    }

    @PostMapping("/profile")
    public ResponseEntity<String> reportProfile(@Valid @RequestBody ReportVM reportVM,
                                                BindingResult bindingResult,
                                                Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        reportService.reportProfile(getAccountIdFrom(principal),
                                    reportVM.getReportedId(),
                                    reportVM.getReportReasonId(),
                                    reportVM.getDescription());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @PostMapping("/match")
    public ResponseEntity<String> reportMatch(@Valid @RequestBody ReportVM reportVM,
                                              BindingResult bindingResult,
                                              Principal principal) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        reportService.reportMatch(getAccountIdFrom(principal),
                                  reportVM.getReportedId(),
                                  reportVM.getReportReasonId(),
                                  reportVM.getDescription());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }


}
