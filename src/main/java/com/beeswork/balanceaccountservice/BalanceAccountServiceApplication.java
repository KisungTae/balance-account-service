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
//				  9. email request param needs validation
//				 10. can't remove tailing spaces in @RequestBody
//				 11. ObjectMapper Singleton make it final, final ObjectMapper objectMapper does it make objectmapper final?
//				 12. Convert.fieldErrorsToJson need Map?
//				 13. messageSource or ValidationMessage for field @Valid, because messagesource dose not seem to map with the validation messages
//				 14. type.mismatch does not occur why? even if you map wrong type through JSON
//				 15. if objectmapper has custom mapping and it is singleton, then object lock occurs?
//				 16. check if account is blocked or not before proceeding to methods in services
//				 17. when updating profile, update location as well because of concurrent transaction, location can be updated at the same time as profile update
//				 18. schedule a function to update swipe_count because if you allow swipe() to plus swipe_count, then the number will be incorrect
//					 if multiple transaction tries to update the same entity. Version should slow down swipe() when many people try to update
//				 19. when create a user at first, you should also create photo_info for the user
//				 20. when save account block, transaction should be [] to prevent block field to be overwritten by saving profiles or rep_photo_key
//				 21. I have added email in LocationDTO and locationVM, AccountQuestionSaveVM and DTO, Recommend()
//				 22. check if email id empty at controller
//				 23. FCMService send notification should be async




// TODO: 04/09/2020	-
//