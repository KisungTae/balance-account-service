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

// TODO: GLOBAL - 1. change the exception message in controlleradvice to messagesource from exception.getmessage()
//				  2. remove createat and updateat in some entities like question, account we dont need them
//				  3. consider validtors or remove duplicates in accountquestionDTO when saveQuestions() in account.save();
//				  4. should I check the size of accountQuestionVMs and throw exception?
//				  5. consider remove email in AccountVM and DTO
//				  6. @requestParam with @validated throws error
//				  7. @ModelAttribute is not mapped with query string in url
//				  8. when use nativequery, type cast warning List<Object> lists = createNativeQuery().getresultlist, what is suppressWarning("unchecked")



// TODO: 04/09/2020	- 1. can't remove tailing spaces in @RequestBody
//					  2. ObjectMapper Singleton make it final, final ObjectMapper objectMapper does it make objectmapper final?
//					  3. Convert.fieldErrorsToJson need Map?
//					  4. messageSource or ValidationMessage for field @Valid, because messagesource dose not seem to map with the validation messages
//					  5. type.mismatch does not occur why? even if you map wrong type through JSON
//					  6. if objectmapper has custom mapping and it is singleton, then object lock occurs?
//