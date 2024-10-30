package org.company.trashambulance.utils;

import org.company.trashambulance.callbacks.CallbackType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class CantUnderstandUtils {
    public static SendMessage getSendMessage(String chatId) {
        SendMessage message = new SendMessage(chatId, Consts.CANT_UNDERSTAND);
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var formButton = new InlineKeyboardButton();
        formButton.setText("Вернуться назад");
        formButton.setCallbackData(CallbackType.MY_FORMS);
        rowInLine.add(formButton);
        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        return message;
    }
}
