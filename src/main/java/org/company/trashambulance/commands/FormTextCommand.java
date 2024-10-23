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
public class FormTextCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(FormTextCommand.class);

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
                user.setState(States.FORM_ADDRESS_STATE);

                userService.saveUser(user);

                stateDataDAO.removeStateDataByUserId("form_text" + "_" + user.getId());
                stateDataDAO.removeStateDataByUserId("form_photo" + "_" + user.getId());
                stateDataDAO.setStateData(user, "form_text", text);

                message.setText(EmojiParser.parseToUnicode("Отлично! :wink: Теперь отправьте боту адрес, где произошла проблема."));

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
