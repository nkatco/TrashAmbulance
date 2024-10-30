package org.company.trashambulance.commands;

import lombok.extern.slf4j.Slf4j;
import org.company.trashambulance.utils.Consts;
import org.company.trashambulance.models.Command;
import org.company.trashambulance.models.TelegramMessage;
import org.company.trashambulance.models.TelegramSendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CommandsHandler {

    private final Map<String, Command> commands;
    @Value("${bot.name}")
    String name;

    public CommandsHandler(@Autowired StartCommand startCommand,
                           @Autowired CheckGroupCommand checkGroupCommand
                           ) {
        this.commands = new HashMap<>();
        commands.put("/start", startCommand);
        commands.put("/start@ao_ecologia_bot", checkGroupCommand);
    }

    public TelegramMessage handleCommands(Update update) {
        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];
        long chatId = update.getMessage().getChatId();

        var commandHandler = commands.get(command);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            return new TelegramSendMessage(new SendMessage(String.valueOf(chatId), Consts.UNKNOWN_COMMAND), String.valueOf(chatId));
        }
    }

}
