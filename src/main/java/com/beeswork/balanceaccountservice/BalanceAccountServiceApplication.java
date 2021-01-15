package com.beeswork.balanceaccountservice;

import com.beeswork.balanceaccountservice.config.properties.TaskExecutorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
@EnableRetry
public class BalanceAccountServiceApplication {

//	@Value("${task-executor.max-pool-size:1}")
//	private int maxPoolSize;
//
//	@Value("${task-executor.queue-capacity:0}")
//	private int queueCapacity;

	private final TaskExecutorProperties taskExecutorProperties;

	@Autowired
	public BalanceAccountServiceApplication(TaskExecutorProperties taskExecutorProperties) {
		this.taskExecutorProperties = taskExecutorProperties;
	}


	@Bean(name="processExecutor")
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(taskExecutorProperties.getMaxPoolSize());
		taskExecutor.setQueueCapacity(taskExecutorProperties.getQueueCapacity());
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}

	public static void main(String[] args) {
		SpringApplication.run(BalanceAccountServiceApplication.class, args);
	}

}





// TODO ************************************************************************************

// TODO			1. change the exception message in exceptionControllerAdvice to messageSource from exception.getMessage()
//  				--> created exceptionResponse() for getting exception message from messageSource for locale
//
// TODO			2. remove createdAt and updatedAt in some entities like question, account we dont need them
//					--> it's okay to keep them

// TODO			3. consider validators or remove duplicates in accountQuestionDTOs when saveQuestions() in account.save();
//					--> changed to Map<Integer, Boolean> in SaveAnswersVM and Map<> does not accept duplicates

// TODO			4. should I check the size of accountQuestionVMs and throw exception?
//					--> @Size(min = 3, max = 3) has been applied to private Map<Integer, Boolean> answers = new HashMap<>();

// TODO			5. consider remove email in AccountVM and DTO
//					--> accountService will have saveEmail() and only SaveProfileVM has email field

// TODO			6. @requestParam with @validated throws error
//					--> now GetMapping() has @ModelAttribute and PostMapping() has @RequestBody which both have bindingResult

// TODO			7. @ModelAttribute is not mapped with query string in url
//					--> It is now mapped, look at the recommend() in AccountController

// TODO			8. when use nativequery, type cast warning List<Object> lists = createNativeQuery().getresultlist, what is suppressWarning("unchecked")
//					--> because getResult() returns raw type of rows, so we can just ignore the warnings

// TODO			9. email request param needs validation
//					--> @Email annotation has been added

// TODO			10. can't remove tailing spaces in @RequestBody
//					--> StringDeserializer has been added to get rid of the tailing spaces

// TODO			11. ObjectMapper Singleton make it final, final ObjectMapper objectMapper does it make objectmapper final?
//					-->

// TODO			12. Convert.fieldErrorsToJson need Map?
//					--> To convert it to json format then, yes we need map in the first place then we serialize it to json

// TODO			13. messageSource or ValidationMessage for field @Valid, because messagesource dose not seem to map with the validation messages
//					--> now it is mapped to messages_ko or messages_en

// TODO			14. type.mismatch does not occur why? even if you map wrong type through JSON
//					--> exception is thrown at prior-controller level, so we just get bad request error, it does not even touch controller

// TODO			15. if objectmapper has custom mapping and it is singleton, then object lock occurs?
//					--> Spring Controller will throw TypeMismatchException when there is a mismatch in parameter, you can handle in controllerAdvice

// TODO			16. check if account is blocked or not before proceeding to methods in services
//					--> checkIfValid() has been added to all methods

// TODO			17. when updating profile, update location as well because of concurrent transaction, location can be updated at the same time as profile update
//					--> @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3,   backoff = @Backoff(delay = 1)) has been added

// TODO			18. create a function that can be scheduled to update swipe_count in account. Because of the Optimistic lock exception, we can't update right away
//					-->

// TODO			19. when create a user at first, you should also create photo_info for the user
//					--> we no longer use photo_info table

// TODO			20. when save account block, transaction should be [] to prevent block field to be overwritten by saving profiles or rep_photo_key
//					--> @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3,   backoff = @Backoff(delay = 1)) has been added

// TODO			21. I have added email in LocationDTO and locationVM, AccountQuestionSaveVM and DTO, Recommend()
//					--> Pretty much every VMs extend AccountIdentity which has accountId and identityToken

// TODO			22. check if email id empty at controller
//					--> email has been replaced by IdentityToken for identity check

// TODO			23. FCMService send notification should be async
//					--> Async has been applied

// TODO			24. minus point after checking the answers of balancegame
//					--> minus point is moved to click()

// TODO			25. check fcm location update when saving profile can be overwritten by saving profiles
//					--> @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3,   backoff = @Backoff(delay = 1)) has been added

// TODO			26. update to have check answers of balancegame and proceeed to create match or not and retunr the result even if it is match
//					--> click() returns empty ClickDTO with clickResult = NOT_CLICK

// TODO			27. update getClickedList to have last update time as field and remove udpatedAt in the ClickedDTO
//					-->

// TODO			28. consider whether to put {error.message} in vm's fields
//					--> only for SaveProfile() because other VMs are not supposed to display error messages to users

// TODO			29. when token is not valid, Firebase returns error response, put try catch in FCMServiceImpl
//					-->

// TODO			30. It seems like the integration runs twice and I don't know why
//					--> because integration test runs at build time and run time, I have removed build time test

// TODO			31. account_question should have history, because a user has answers on old question and try to click and should have history to match, so, while balanceGame retrieves only the questions currently selected
//					--> logic has been modified to reflect this, when no question matches then throws QuestionSetChangedException()

// TODO			32. consider CSRF token implementation
//					-->

// TODO			33. when create account very first time, you should give it default location
//					-->

// TODO			34. in lambda for photos, when they update account plus version++ so that spring does  not overwrite
//					--> has been applied to GetPreSignedURL lambda function

// TODO 		35. findWithQuestions() returns null when there is no accountQuestions, but checkIfValid() throws AccountNotFoundException
//					--> includes accounts with no questions by including account.question.accountId is null in findWithAccountQuestions()

// TODO 		36. add saveEmail after implementing login logic, if accountType == Google or Naver, then don't let user change email, you should add logic in saveProfile() in AccountService
//					--> saveEmail() has been implemented

// TODO			37. add log to services and controllers

// TODO 		38. check unmatch in regards to check blocked and identity token
//					--> does not need to check blocked and identity token

// TODO 		39. create a function to list "click" because you don't store the clickedId before click() in android level
//					--> listClick() has been implemented

// TODO			40. updatedAt.after() does not include equal, you should subtract 1 milisecond from the date to include equal, check listclicked, listClick, listMatches
//					-->  fetchedAt = DateUtils.addMilliseconds(fetchedAt, -1); has bee added

// TODO			41. implement randomQuestions for initializing balancegamequestions at first, and when no question is found in listQuestions then call randomQuestions and pass new random questiosn
//					--> listRandomQuestions() has been implemented

// TODO 		43. add logic to plus milisseocnds of photo name when there is ducpiate in the lamdba of getPresignuro
//					-->	fetchedAt = DateUtils.addMilliseconds(fetchedAt, -1); implemented

// TODO			45. delete, block, update photo should update the updatedAt column so that listClicked and listMatches can query on them
//					--> implemented

// TODO			47. modify logic to check answers in clickservice
//					--> long findAllByAnswer(UUID accountId, Map<Integer, Boolean> answers) in accountQuestionDAO

// TODO			48. add deleted and blocked field to matchProject and clickedProject
//					--> implemented

// TODO 		49. when user load cards and not go through all the cards and close the app and open again, then the index starts with 2 not 1 which has card left

// TODO			50. limit the size of message in stomp

// TODO			51. check anything calling account because you can use cache so, change to findById(accountId) then check identitytoken separately not query together otherwise you can't use cache benefits

// TODO			52. consider how to retrieve message when enter to a chat, and consider changing createdAt in message generated on the server


// TODO add to android: when swipeclicked exists exception is thrown then you should add it to the click list in Adnroid app, fetchedat not stored in shared preference but just query clicked, matched, click and get the latest one
