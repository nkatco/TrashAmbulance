package org.company.trashambulance.handlers;

import org.company.trashambulance.utils.Consts;
import org.company.trashambulance.callbacks.CallbackHandler;
import org.company.trashambulance.callbacks.CallbackType;
import org.company.trashambulance.callbacks.about.AboutCallback;
import org.company.trashambulance.callbacks.add_form.AddFormCallback;
import org.company.trashambulance.callbacks.my_forms.MyFormCallback;
import org.company.trashambulance.callbacks.my_forms.MyFormsCallback;
import org.company.trashambulance.callbacks.start.StartCallback;
import org.company.trashambulance.models.TelegramMessage;
import org.company.trashambulance.models.TelegramSendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Component
public class CallbacksHandler {

    private final Map<String, CallbackHandler> callbacks;

    public CallbacksHandler(@Autowired  AboutCallback aboutCallback,
                            @Autowired StartCallback startCallback,
                            @Autowired AddFormCallback addFormCallback,
                            @Autowired MyFormsCallback myFormsCallback,
                            @Autowired MyFormCallback myFormCallback
                            ) {
        this.callbacks = new HashMap<>();
        callbacks.put(CallbackType.ABOUT_BOT, aboutCallback);
        callbacks.put(CallbackType.START_BUTTON, startCallback);
        callbacks.put(CallbackType.ADD_FORM, addFormCallback);
        callbacks.put(CallbackType.MY_FORMS, myFormsCallback);
        callbacks.put(CallbackType.MY_FORM, myFormCallback);
    }

    public TelegramMessage handleCallbacks(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        SendMessage answer = null;
        TelegramMessage telegramMessage = null;
        try {
            CallbackHandler callbackBiFunction = callbacks.get(callbackData);
            if (callbackBiFunction != null) {
                telegramMessage = callbackBiFunction.apply(update);
            } else {
                for (String key : callbacks.keySet()) {
                    if (callbackData.startsWith(key)) {
                        callbackBiFunction = callbacks.get(key);
                        break;
                    }
                }
                if(callbackBiFunction != null) {
                    telegramMessage = callbackBiFunction.apply(update);
                    telegramMessage.setChatId(String.valueOf(chatId));
                } else {
                    answer = new SendMessage();
                    answer.setChatId(chatId);
                    answer.setText(Consts.ERROR);
                    telegramMessage = new TelegramSendMessage(answer, String.valueOf(chatId));
                    telegramMessage.setChatId(String.valueOf(chatId));
                }
            }
        } catch (Exception e) {
            answer = new SendMessage();
            answer.setChatId(chatId);
            answer.setText(Consts.ERROR);
            e.printStackTrace();
        }
        if(telegramMessage != null) {
            telegramMessage.setChatId(String.valueOf(chatId));
            assert answer != null;
            return telegramMessage;
        } else {
            answer = new SendMessage();
            answer.setText(Consts.ERROR);
            answer.setChatId(update.getCallbackQuery().getMessage().getChatId());
            return new TelegramSendMessage(answer, String.valueOf(chatId));
        }
    }
}
