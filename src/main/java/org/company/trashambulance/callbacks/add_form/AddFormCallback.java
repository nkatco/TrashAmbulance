package org.company.trashambulance.callbacks.add_form;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.company.trashambulance.callbacks.CallbackHandler;
import org.company.trashambulance.callbacks.CallbackType;
import org.company.trashambulance.models.TelegramMessage;
import org.company.trashambulance.models.TelegramSendMessage;
import org.company.trashambulance.models.User;
import org.company.trashambulance.services.UserService;
import org.company.trashambulance.states.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AddFormCallback implements CallbackHandler {

    @Autowired
    public UserService userService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        if(user.getState().equals(States.BASIC_STATE)) {
            user.setState(States.FORM_STATE);

            userService.saveUser(user);

            String text = EmojiParser.parseToUnicode(":page_facing_up: Отправьте фотографию с описанием проблемы и адресом.");

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            var channelButton = new InlineKeyboardButton();

            rowInLine = new ArrayList<>();
            channelButton = new InlineKeyboardButton();
            channelButton.setText("Вернуться назад");
            channelButton.setCallbackData(CallbackType.START_BUTTON);
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);

            markupInLine.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInLine);

            message.setText(text);
            return new TelegramSendMessage(message, String.valueOf(chatId));
        }
        return null;
    }
}
