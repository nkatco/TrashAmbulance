package org.company.trashambulance.handlers;

import lombok.extern.slf4j.Slf4j;
import org.company.trashambulance.utils.Consts;
import org.company.trashambulance.commands.FormAddressCommand;
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
            @Autowired FormTextCommand formTextCommand,
            @Autowired FormAddressCommand formAddressCommand
            ) {
        this.states = new HashMap<>();
        states.put(States.FORM_STATE, formTextCommand);
        states.put(States.FORM_ADDRESS_STATE, formAddressCommand);
    }

    public TelegramMessage handleCommands(Update update) {
        String messageText = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        User user = userService.getUserByTelegramId(update.getMessage().getFrom().getId());

        var commandHandler = states.get(user.getState());
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            return new TelegramSendMessage(new SendMessage(chatId, Consts.CANT_UNDERSTAND), chatId);
        }
    }
}
