package com.beeswork.balanceaccountservice.restcontroller;


import com.beeswork.balanceaccountservice.dto.setting.PushSettingDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.setting.SettingService;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import com.beeswork.balanceaccountservice.vm.setting.SavePushSettingsVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/setting")
public class SettingController extends BaseController {

    private final SettingService settingService;

    @Autowired
    public SettingController(ObjectMapper objectMapper,
                             ModelMapper modelMapper,
                             SettingService settingService) {
        super(objectMapper, modelMapper);
        this.settingService = settingService;
    }

    @PostMapping("/push")
    public ResponseEntity<String> savePushSettings(@Valid @RequestBody SavePushSettingsVM savePushSettingsVM,
                                                   BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        settingService.savePushSettings(savePushSettingsVM.getAccountId(),
                                        savePushSettingsVM.getIdentityToken(),
                                        savePushSettingsVM.getMatchPush(),
                                        savePushSettingsVM.getClickedPush(),
                                        savePushSettingsVM.getChatMessagePush());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/push")
    public ResponseEntity<String> getPushSetting(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
                                                 BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        PushSettingDTO pushSettingDTO = settingService.getPushSetting(accountIdentityVM.getAccountId(), accountIdentityVM.getIdentityToken());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(pushSettingDTO));
    }


}
