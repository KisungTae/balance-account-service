package com.beeswork.balanceaccountservice.dto.push;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;

import java.util.Locale;


@Getter
@Setter
public abstract class AbstractPush implements Push {

    public enum Type {
        MISSED,
        CLICKED,
        MATCHED,
        CHAT_MESSAGE
    }

    protected static final String PUSH_TITLE_CODE = "push.title";
    protected static final String PUSH_TYPE       = "type";

    protected Type type;

    public AbstractPush(Type type) {
        this.type = type;
    }

    public abstract Message buildFCMMessage(Message.Builder messageBuilder,
                                            MessageSource messageSource,
                                            String fcmToken,
                                            Locale locale);

    protected void setFCMNotification(Message.Builder messageBuilder,
                                      MessageSource messageSource,
                                      Locale locale,
                                      String pushBodyCode,
                                      String[] pushBodyArguments) {
        String body = messageSource.getMessage(pushBodyCode, pushBodyArguments, locale);
        String title = messageSource.getMessage(PUSH_TITLE_CODE, null, locale);
        messageBuilder.setNotification(Notification.builder().setTitle(title).setBody(body).build());
        messageBuilder.putData(PUSH_TYPE, type.toString());
    }
}
