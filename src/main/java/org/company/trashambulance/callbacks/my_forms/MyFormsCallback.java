package org.company.trashambulance.callbacks.my_forms;

import lombok.RequiredArgsConstructor;
import org.company.trashambulance.callbacks.CallbackHandler;
import org.company.trashambulance.callbacks.CallbackType;
import org.company.trashambulance.models.Form;
import org.company.trashambulance.models.TelegramMessage;
import org.company.trashambulance.models.TelegramSendMessage;
import org.company.trashambulance.models.User;
import org.company.trashambulance.services.FormService;
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
public class MyFormsCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public FormService formService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        user.setState(States.BASIC_STATE);

        userService.saveUser(user);

        String text = "Здесь отображаются все ваши заявления.\n\nДля просмотра информации о заявлении, нажмите на него.";

        List<Form> forms = formService.getFormsByUserId(user.getId());

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var formButton = new InlineKeyboardButton();

        for (int i = 0; i < forms.size(); i++) {
            formButton = new InlineKeyboardButton();
            formButton.setText("Заявление от " + forms.get(i).getFormattedCreationDate());
            formButton.setCallbackData(CallbackType.MY_FORM + forms.get(i).getId());
            rowInLine.add(formButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();
        }
        formButton = new InlineKeyboardButton();
        formButton.setText("Вернуться назад");
        formButton.setCallbackData(CallbackType.START_BUTTON);
        rowInLine.add(formButton);
        formButton = new InlineKeyboardButton();
        formButton.setText("Заполнить заявление");
        formButton.setCallbackData(CallbackType.ADD_FORM);
        rowInLine.add(formButton);
        rowsInLine.add(rowInLine);
        rowInLine = new ArrayList<>();

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        message.setText(text);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
