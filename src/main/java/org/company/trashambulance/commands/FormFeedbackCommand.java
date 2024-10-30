package org.company.trashambulance.commands;

import com.vdurmont.emoji.EmojiParser;
import org.company.trashambulance.daos.StateDataDAO;
import org.company.trashambulance.models.*;
import org.company.trashambulance.services.FormService;
import org.company.trashambulance.services.ForwardDataService;
import org.company.trashambulance.services.UserService;
import org.company.trashambulance.states.States;
import org.company.trashambulance.utils.TelegramBotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class FormFeedbackCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(FormFeedbackCommand.class);

    @Autowired
    private TelegramBotUtils telegramBotUtils;
    @Autowired
    private ForwardDataService forwardDataService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        long adminId = update.getMessage().getFrom().getId();
        SendMessage message = new SendMessage();

        try {
            try {
                ForwardData forwardData = forwardDataService.getForwardDataByMessageId(Long.valueOf(update.getMessage().getReplyToMessage().getMessageId()));
                if(forwardData != null) {
                    String text = EmojiParser.parseToUnicode("<b>ОТВЕТ РЕГОПЕРАТОРА</b>\n"
                            + "Заявление от: " + forwardData.getForm().getFormattedCreationDate() + "\n"
                            + "\n\n"
                            + update.getMessage().getText());

                    telegramBotUtils.sendMessageForChat(String.valueOf(forwardData.getChatId()), text);

                    message.setText(EmojiParser.parseToUnicode("Ответ доставлен."));
                    message.setReplyToMessageId(update.getMessage().getMessageId());

                    logger.info("Feedback: {}", adminId);
                } else {
                    message.setText(EmojiParser.parseToUnicode("Ответ не доставлен: заявление уже закрыто, либо уже не сущестует."));
                    message.setReplyToMessageId(update.getMessage().getMessageId());
                }
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
