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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
//					--> listMatches() will retrieve all the chatMessages unfetched or unreceived

// TODO 		53. chagne match to have id increment so that no need to createdAt when fetching list of matches, fetch by id
//					--> can't because unmatch does not update id then no way to retrieve newly updated match so go for dates

// TODO			54. encrypt chat message

// TODO			55. check daos for match, chat, account because they are all cached

// TODO			56. check unmatch() in matchService in regards to cache findMatchById
//					--> changed to use matchDAO.findById() from findPair()

// TODO			57. change to throw badrequestexception if field errors inclde accountId and IdentityToekn in base controller

// TODO			58. check delete photo in account service and the findWithPhotosAndAccountQuestions()
//

// TODO			59. retrieve chatMessages make sent body = null
//					--> implemented

// TODO			60. only chatMessages within 2 weeks are retrieved, so check lastChatMesageCreated > (today-2weeks)
//					--> chatMessages are deleted after 1 week

// TODO			61. save configuration of android, and fetchedAt datetimes,

// TODO			62. chatmessage has chatmessages fetched and received tables
//					--> added fetched and received to chatMessage table

// TODO			63. when app delted, send out the last read message date for each chat and save them in case of when app reinstalled
//					--> it's not possible to send a message when app deleted

// TODO 		64. chat workflow, send --> receipt || receive --> ack --> update chat_message received column
//					list matches --> android query to get the last synced message's createdat,
//					server query chat_message where sender = accountid and created > last synced message's createdat
//					query chat_message where recipient = accountId and received = false
//					--> listMatches() query chatMessages unreceived or unfetched

// TODO			65. decide how many minutes subtract from lastChatMessagecreated, lastAccountudpatedAt .. etc

// TODO			66. change repPhotoKey in Account, so when create account at very first time, put empty string for repPhotoKey
//

// TODO			67. remove chatId in chatMessage and findAllSentAfter()

// TODO			68. update received of chatmessage could throw exception when version is on the entity, should be fine,
//  				because all of them update received = true does not matter if first or second one updates the same entitiy it will result in received = true
//					--> chatMessage now had fetched as well so implemented with Pessimistic Lock of Write

// TODO			69. make sure only updating the proflile and photo updated the updatedAt of account for flagging the account in match list when account profile or photo updated
//					--> updated_at is updated from delete(), resetRepPhoto()

// TODO 	 	70. findAllClickedAfter should return valid accounts so delete deleted, blocked fields in ClickedProjection
//					--> it should return invalid accounts so that client can remove invalid clicked in the list

// TODO			70. click() if two users click at the same time, then one can't see another swipe clicked which results in not create matches
//					--> prevent deadlock login implemented by write lock on account

// TODO			70. clickedProjection and clickProjection change to SwipeDTO and deleted the block field in the query
//					--> implemented

// TODO			70. findAllClickedAfter is convoluted, considering matched field to swipe so that retrieve swipes where matched = false, clicked = true
//					--> implemented

// TODO 		71. block account make the account's deleted field = true as well so that only the deleted field can be used for other users vadiliaty
//					only the users themselves get the blocked exception, and the other users will get deleted on fetched something, and blocking a user also makes
//					social_login, profile, account blocked and deleted = true

// TODO			72. add deleted field in match and exclude deleted matches in listMatches()
//					--> implemented

// TODO			73. add synced in chatMessage and change findAllAfter to findAllUnsynced
//					--> implemented

// TODO			74. delete chatMessages when they are synced and received and > 1 weeks, and
//					chatMessages unsycned or unfetched for 1 month

// TODO			75. if listMatches > 10mb then drop some chatmessages and make full = false, then clicent will request again
//					--> will implement when this happens, it will rarely happen

// TODO			76. before saving chatMessage to database, check if there is a duplicate for sent message then if exists,
//  				then just return the created_at and Id
//					--> implemented

// TODO			78. back up chatMessages, create a table for back-up and encrypt and compress at client side and upload it to s3,
//  				then delete back-up that is not used for 15 days based on the table

// TODO			79. implement super_click logic, it's just skip the balance game check in swipe()

// TODO			80. limit the name of account to < 30mb 10 characters
//					--> limited the name of account to 15 characters as an alphabet takes up a character

// TODO			81. unmatch() makes match's deleted = true and unmatched = true but the other party's match's unmatched = true only
//					--> implemented

// TODO			82. split account into several table, so that I can cache small account not the whole account
//					--> implemented

// TODO			83. remove columns like     @Column(name = "matched_id", insertable = false, updatable = false)
//    				private UUID matchedId; because it takes up bytes on memory when cached
//					--> removed, only two columns stay in ChatMessage and Match

// TODO			84. entity changes object type to primitive, like Long -> long, Boolean -> boolean
//					--> implemented

// TODO			85. firebaseservice should be called from controller level, because click() could be called twice when versionexception thrown then two notification will be sent
//					--> implemented

// TODO			86. get rid of question instead, answers's top and bottom options have questions like https://post.naver.com/viewer/postView.nhn?volumeNo=29296262&memberNo=38753951
//					--> you can just put empty string to description in question table, then client will not display any description

// TODO			87. move out notification to notiication service which has firebaseService and stompService
//					--> implemented

// TODO			88. deleteAccount() should also delete social_login, profile, push_notificaiton as well as account
//					--> implemented

// TODO			89. deletePhotosAsync() in S3Service throws one or more objects could not be dleted eexception

// TODO			90. when create chatMessage, create sentChatMessage as well
//					--> implemented

// TODO			91. consider offsetTime = milliseconds otherwise listMatches() in android will keep increasing rowId of the same match

// TODO			92. listMatches deleted just set unmatched = true and get rid of deleted in matchDTO
//					--> implemented, android also updated accordingly

// TODO			93. click() should return matchDTO? and then there no rabbit queue for user then make it push and push it through FCM
//					--> click returns clickDTO which contains matchDTO for matcher and matched

// TODO			94. swipe_point should be from database not from static variable so that I can easily change it without rebooting accountservice
//					--> implemented

// TODO			95. separte point related fields from Account to Point or soemthing table
//					--> implemented

// TODO			96. make max age = 0 when it is 80 which is max age, calculate (current year - max age year) and if == 80 then max year = 0 in recommend()
//					--> client side will implement

// TODO 		97. listMatches, listClicks, should include accountID in MatchDTO

// TODO			97. test push in regards to setting matchPUsh, clickedPUsh etc..

// TODO			98. when logged, and profile does not exists or enabled = false, then send profile and photos as well so that users can continie with profile set up 

// TODO			99. check how it works, isAccountNonExpired in Account, should I use for delete, check delete fun

// TODO			100. validateAccount should be in security check not each service?


// TODO			Chat Business rules
//				1. 	send, receipt and ack || receive and ack
//				2. 	listMatches retrieve only the chatMessages that are not received or synced
//				2. 	client checks listMatches if it's fill list, if false, then recursion function to list chatMessages,
//					full depends on the size of chatMessages 30000?
//				2. 	listMatches return sentChatMessages, grab the largest chatMessageId from sentChatMessages and then
//					if chatMessages in database are in sending status and < the largest chatMessageId then make them to error status
//				3. 	when login to the app, check if there is active back-up available, if yes, then direct the user to back-up page
//				   	before they get into the app main page like swipe page
//				4.  instead of listChatMessages just use listMatches even if a user comes back to the app's chat page from background
//				5.	add read column to chatMessage for future implementation and fetched
//				6.	add deleted column to matches
//				7. 	back-up includes matches and chatMessages so that we don't have to update lastChatmessageId per match on the server
//				8. 	sent or receive message, check match's opened if false then true and set updated_at so that next listMatches include the updated Matches,
//					on client side, when received message or receipt of sent message then make match's opened = true on client side
//				9. 	match does not track the active field, if users delete app and no backup, then the matches without unreceived or unsynced messages will be treated as new one
//				10. match's created_at will be assigned at client side, so listMatch does not return updated_at field
//				11.

// TODO add to android: when swipeclicked exists exception is thrown then you should add it to the click list in Adnroid app, fetchedat not stored in shared preference but just query clicked, matched, click and get the latest one




// TODO AWS ===================================================
// TODO: 1. check CORS on S3 for presigned url upload and read