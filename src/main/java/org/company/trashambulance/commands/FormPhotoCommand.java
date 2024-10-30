package org.company.trashambulance.commands;

import com.vdurmont.emoji.EmojiParser;
import org.company.trashambulance.callbacks.CallbackType;
import org.company.trashambulance.models.*;
import org.company.trashambulance.services.FormService;
import org.company.trashambulance.services.UserService;
import org.company.trashambulance.states.States;
import org.company.trashambulance.utils.TelegramBotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class FormPhotoCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(FormPhotoCommand.class);

    @Autowired
    private UserService userService;
    @Autowired
    private FormService formService;
    @Lazy
    @Autowired
    public TelegramBotUtils telegramBotUtils;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        try {
            Document document = update.getMessage().getDocument();

            String fileId = null;
            if (document != null) {
                fileId = document.getFileId();
            } else if (update.getMessage().hasPhoto()) {
                fileId = update.getMessage().getPhoto().get(update.getMessage().getPhoto().size() - 1).getFileId();
            }

            if (fileId == null) {
                message.setText("Не удалось загрузить изображение. Пожалуйста, отправьте фотографию без сжатия с текстом.");
                return new TelegramSendMessage(message, String.valueOf(chatId));
            }

            try {
                User user = userService.getUserByTelegramId(userId);
                user.setState(States.BASIC_STATE);

                telegramBotUtils.sendMessageForUser(user, "Изображение загружается, подождите...");

                userService.saveUser(user);
                if (update.getMessage().getCaption() != null && !update.getMessage().getCaption().isEmpty()) {
                    String text = EmojiParser.parseToUnicode(update.getMessage().getCaption());
                    String photo = null;
                    if (!text.isEmpty()) {
                        logger.info("Downloading watermark image for user: {}", userId);
                        photo = (telegramBotUtils.downloadPhoto(fileId));
                        if (photo == null) {
                            message.setText("Ошибка.");
                            return new TelegramSendMessage(message, String.valueOf(chatId));
                        }
                    } else {
                        message.setText("Ошибка.");
                        return new TelegramSendMessage(message, String.valueOf(chatId));
                    }

                    Form form = new Form();
                    form.setText(text);
                    form.setUser(user);
                    form.setMessageId(update.getMessage().getMessageId());
                    if(photo != null && !photo.isEmpty()) {
                        form.setPhoto(photo);
                    }
                    form.setChatId(chatId);

                    formService.saveForm(form);

                    message.setText(EmojiParser.parseToUnicode("Заявление успешно доставлено, спасибо за обращение :relaxed:"));

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
                    return new TelegramSendMessage(message, String.valueOf(chatId));
                } else {
                    user = userService.getUserByTelegramId(userId);
                    user.setState(States.FORM_STATE);
                    userService.saveUser(user);
                    message.setText(EmojiParser.parseToUnicode("Изображение должно содержать текст!"));
                    return new TelegramSendMessage(message, String.valueOf(chatId));
                }
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
