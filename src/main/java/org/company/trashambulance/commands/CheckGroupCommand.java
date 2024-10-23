package org.company.trashambulance.commands;

import lombok.RequiredArgsConstructor;
import org.company.trashambulance.models.Command;
import org.company.trashambulance.models.TelegramMessage;
import org.company.trashambulance.models.TelegramSendMessage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Component
public class CheckGroupCommand implements Command {

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();

        message.setText("ChatId: " + update.getMessage().getChatId());
        message.setChatId(String.valueOf(chatId));

        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}