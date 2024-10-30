package org.company.trashambulance.handlers;

import lombok.extern.slf4j.Slf4j;
import org.company.trashambulance.commands.FormFeedbackCommand;
import org.company.trashambulance.models.Command;
import org.company.trashambulance.models.TelegramMessage;
import org.company.trashambulance.models.TelegramSendMessage;
import org.company.trashambulance.services.UserService;
import org.company.trashambulance.states.States;
import org.company.trashambulance.utils.CantUnderstandUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ChatHandler {

    @Autowired
    private UserService userService;
    private final Map<String, Command> states;

    public ChatHandler(@Autowired FormFeedbackCommand formFeedbackCommand) {
        this.states = new HashMap<>();
        states.put(States.ADMIN_CHAT_STATE, formFeedbackCommand);
    }

    public TelegramMessage handleCommands(Update update) {
        String messageText = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        var commandHandler = states.get(States.ADMIN_CHAT_STATE);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            SendMessage message = CantUnderstandUtils.getSendMessage(chatId);
            return new TelegramSendMessage(message, chatId);
        }
    }
}
