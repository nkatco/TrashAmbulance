package org.company.trashambulance.callbacks.my_forms;

import lombok.RequiredArgsConstructor;
import org.company.trashambulance.callbacks.CallbackHandler;
import org.company.trashambulance.callbacks.CallbackType;
import org.company.trashambulance.models.*;
import org.company.trashambulance.services.FormService;
import org.company.trashambulance.services.UserService;
import org.company.trashambulance.states.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MyFormCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public FormService formService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String callbackData = update.getCallbackQuery().getData();

        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        user.setState(States.BASIC_STATE);
        userService.saveUser(user);

        String id = callbackData.substring(CallbackType.MY_FORM.length());
        Form form = formService.getFormById(id);

        String text = "ЗАЯВЛЕНИЕ " + form.getFormattedCreationDate() + "\n" + form.getText();

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

        message.setText(text);
        if(form.getPhoto() != null) {
            SendPhoto message1 = new SendPhoto();
            message1.setChatId(message.getChatId());
            message1.setCaption(message.getText());
            message1.setPhoto(new InputFile(new File(form.getPhoto())));
            message1.setReplyMarkup(markupInLine);
            return new TelegramSendPhoto(message1, String.valueOf(chatId));
        }
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
