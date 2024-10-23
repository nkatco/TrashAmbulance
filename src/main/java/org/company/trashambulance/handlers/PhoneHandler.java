package org.company.trashambulance.handlers;

import lombok.extern.slf4j.Slf4j;
import org.company.trashambulance.utils.Consts;
import org.company.trashambulance.commands.UserAddCommand;
import org.company.trashambulance.models.Command;
import org.company.trashambulance.models.TelegramMessage;
import org.company.trashambulance.models.TelegramSendMessage;
import org.company.trashambulance.states.StateData;
import org.company.trashambulance.states.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
@Slf4j
public class PhoneHandler {

    private final Map<String, Command> commands;
    @Autowired
    private StateData stateData;

    public PhoneHandler(@Autowired UserAddCommand userAddCallback) {
        this.commands = Map.of(
                States.ADD_PHONE_USER, userAddCallback
        );
    }
    public TelegramMessage handlePhone(Update update) {
        String state = stateData.getCurrentState();
        long chatId = update.getMessage().getChatId();

        var commandHandler = commands.get(state);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            return new TelegramSendMessage(new SendMessage(String.valueOf(chatId), Consts.UNKNOWN_COMMAND), String.valueOf(chatId));
        }
    }
}
