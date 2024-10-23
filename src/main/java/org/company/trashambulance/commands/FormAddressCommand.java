package org.company.trashambulance.commands;

import com.vdurmont.emoji.EmojiParser;
import org.company.trashambulance.callbacks.CallbackType;
import org.company.trashambulance.daos.StateDataDAO;
import org.company.trashambulance.models.*;
import org.company.trashambulance.services.FormService;
import org.company.trashambulance.services.UserService;
import org.company.trashambulance.states.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class FormAddressCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(FormAddressCommand.class);

    @Autowired
    private UserService userService;
    @Autowired
    private FormService formService;
    @Autowired
    private StateDataDAO stateDataDAO;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        SendMessage message = new SendMessage();

        try {
            String text = EmojiParser.parseToUnicode(update.getMessage().getText());
            try {
                User user = userService.getUserByTelegramId(userId);
                user.setState(States.BASIC_STATE);

                userService.saveUser(user);

                StateData formTextData = stateDataDAO.getStateDataByUserId("form_text" + "_" + user.getId());
                String formText = (formTextData != null) ? (String) formTextData.getData() : null;

                StateData formPhotoData = stateDataDAO.getStateDataByUserId("form_photo" + "_" + user.getId());
                String formPhoto = (formPhotoData != null) ? (String) formPhotoData.getData() : null;
                stateDataDAO.removeStateDataByUserId("form_text" + "_" + user.getId());
                stateDataDAO.removeStateDataByUserId("form_photo" + "_" + user.getId());

                Form form = new Form();
                form.setText(formText);
                form.setUser(user);
                form.setMessageId(update.getMessage().getMessageId());
                if(formPhoto != null && !formPhoto.isEmpty()) {
                    form.setPhoto(formPhoto);
                }
                form.setAddress(text);
                form.setChatId(chatId);

                formService.saveForm(form);

                message.setText("Заявление успешно доставлено.");
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

                logger.info("Form save successfully for user: {}", userId);
            } catch (Exception e) {
                message.setText("Произошла какая-то ошибка, введите /start и обратитесь к администратору.");
                System.out.println(e);
            }
        } catch (Exception e) {
            message.setText("Неверный ввод.");
        }

        message.setChatId(chatId);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
