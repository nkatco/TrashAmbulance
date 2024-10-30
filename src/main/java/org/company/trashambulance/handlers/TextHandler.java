package org.company.trashambulance.handlers;

import lombok.extern.slf4j.Slf4j;
import org.company.trashambulance.utils.CantUnderstandUtils;
import org.company.trashambulance.commands.FormTextCommand;
import org.company.trashambulance.models.Command;
import org.company.trashambulance.models.TelegramMessage;
import org.company.trashambulance.models.TelegramSendMessage;
import org.company.trashambulance.models.User;
import org.company.trashambulance.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.company.trashambulance.states.States;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TextHandler {

    @Autowired
    private UserService userService;
    private final Map<String, Command> states;

    public TextHandler(
            @Autowired FormTextCommand formTextCommand
            ) {
        this.states = new HashMap<>();
        states.put(States.FORM_STATE, formTextCommand);
    }

    public TelegramMessage handleCommands(Update update) {
        String messageText = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        User user = userService.getUserByTelegramId(update.getMessage().getFrom().getId());

        var commandHandler = states.get(user.getState());
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            SendMessage message = CantUnderstandUtils.getSendMessage(chatId);
            return new TelegramSendMessage(message, chatId);
        }
    }
}
