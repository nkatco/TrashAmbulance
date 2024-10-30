package org.company.trashambulance.commands;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.company.trashambulance.models.Command;
import org.company.trashambulance.models.TelegramMessage;
import org.company.trashambulance.models.TelegramSendPhoto;
import org.company.trashambulance.models.User;
import org.company.trashambulance.services.BannerService;
import org.company.trashambulance.services.UserService;
import org.company.trashambulance.states.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.company.trashambulance.callbacks.CallbackType;

@RequiredArgsConstructor
@Component
public class StartCommand implements Command {

    @Autowired
    private UserService userService;
    @Autowired
    private BannerService bannerService;

    public static SendPhoto getStartCommand(BannerService bannerService) {
        SendPhoto message = new SendPhoto();
        String answer = EmojiParser.parseToUnicode("Добро пожаловать в бот помощи по вывозу ТКО");

        message.setCaption(answer);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        // Первая линия

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var addFormButton = new InlineKeyboardButton();
        addFormButton.setText(EmojiParser.parseToUnicode(":articulated_lorry:" + " Заполнить заявление"));
        addFormButton.setCallbackData(CallbackType.ADD_FORM);
        rowInLine.add(addFormButton);
        rowsInLine.add(rowInLine);

        // Формирование клавиатуры

        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);
        message.setPhoto(new InputFile(bannerService.getImage()));
        return message;
    }

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        SendPhoto message = getStartCommand(bannerService);

        User user = userService.getUserByTelegramId(userId);
        user.setState(States.BASIC_STATE);
        userService.saveUser(user);

        message.setChatId(String.valueOf(chatId));

        return new TelegramSendPhoto(message, String.valueOf(chatId));
    }
}