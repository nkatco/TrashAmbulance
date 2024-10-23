package org.company.trashambulance.commands;

import com.vdurmont.emoji.EmojiParser;
import org.company.trashambulance.utils.TelegramBotUtils;
import org.company.trashambulance.daos.StateDataDAO;
import org.company.trashambulance.models.*;
import org.company.trashambulance.services.UserService;
import org.company.trashambulance.states.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class FormPhotoCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(FormPhotoCommand.class);

    @Autowired
    private UserService userService;
    @Autowired
    private StateDataDAO stateDataDAO;
    @Lazy
    @Autowired
    public TelegramBotUtils telegramBotUtils;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if(update.getMessage().getCaption() == null) {

        }

        try {
            Document document = update.getMessage().getDocument();
            try {
                User user = userService.getUserByTelegramId(userId);
                user.setState(States.FORM_ADDRESS_STATE);

                userService.saveUser(user);

                String text = EmojiParser.parseToUnicode(update.getMessage().getCaption());

                if (document != null & !text.isEmpty()) {
                    logger.info("Downloading watermark image for user: {}", userId);
                    String photo = (telegramBotUtils.downloadPhoto(document.getFileId()));
                    if (photo != null) {
                        stateDataDAO.removeStateDataByUserId("form_text" + "_" + user.getId());
                        stateDataDAO.removeStateDataByUserId("form_photo" + "_" + user.getId());
                        stateDataDAO.setStateData(user, "form_text", text);
                        stateDataDAO.setStateData(user, "form_photo", photo);
                    } else {
                        message.setText("Ошибка.");
                        return new TelegramSendMessage(message, String.valueOf(chatId));
                    }
                } else {
                    message.setText("Ошибка.");
                    return new TelegramSendMessage(message, String.valueOf(chatId));
                }

                message.setText(EmojiParser.parseToUnicode("Отлично! :wink: Теперь отправьте боту адрес, где произошла проблема."));

                logger.info("Form save successfully for user: {}", userId);
                return new TelegramSendMessage(message, String.valueOf(chatId));
            } catch (Exception e) {
                logger.error("Error while setting watermark for user: {}", userId, e);
                message.setText("An error occurred, please enter /start and contact the administrator.");
            }
        } catch (Exception e) {
            logger.error("Invalid input from user: {}", userId, e);
            message.setText("Невалидный ввод.");
        }

        message.setChatId(chatId);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
