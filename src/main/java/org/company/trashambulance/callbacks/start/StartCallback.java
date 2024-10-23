package org.company.trashambulance.callbacks.start;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.company.trashambulance.callbacks.CallbackHandler;
import org.company.trashambulance.callbacks.CallbackType;
import org.company.trashambulance.commands.StartCommand;
import org.company.trashambulance.models.TelegramMessage;
import org.company.trashambulance.models.TelegramSendMessage;
import org.company.trashambulance.models.TelegramSendPhoto;
import org.company.trashambulance.models.User;
import org.company.trashambulance.services.RandomFactsService;
import org.company.trashambulance.services.UserService;
import org.company.trashambulance.states.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class StartCallback implements CallbackHandler {

    @Autowired
    private UserService userService;
    @Autowired
    private RandomFactsService randomFactsService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        long userId = update.getCallbackQuery().getFrom().getId();
        SendPhoto message = StartCommand.getStartCommand(randomFactsService);
        User user = userService.getUserByTelegramId(userId);
        user.setState(States.BASIC_STATE);
        userService.saveUser(user);
        message.setChatId(chatId);

        return new TelegramSendPhoto(message, String.valueOf(chatId));
    }
}
