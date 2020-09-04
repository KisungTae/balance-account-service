package com.beeswork.balanceaccountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BalanceAccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BalanceAccountServiceApplication.class, args);
	}

}





// TODO ************************************************************************************

// TODO: 04/09/2020	- 1. can't remove tailing spaces in @RequestBody
//					  2. ObjectMapper Singleton make it final, final ObjectMapper objectMapper does it make objectmapper final?
//					  3. Convert.fieldErrorsToJson need Map?
//					  4. messageSource or ValidationMessage for field @Valid
//					  5. type.mismatch does not occur why? even if you map wrong type through JSON