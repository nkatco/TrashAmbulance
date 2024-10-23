package org.company.trashambulance.commands;

import lombok.RequiredArgsConstructor;
import org.company.trashambulance.models.*;
import org.company.trashambulance.services.RandomFactsService;
import org.company.trashambulance.services.UserService;
import org.company.trashambulance.states.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Component
public class UserAddCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(UserAddCommand.class);

    @Autowired
    private UserService userService;
    @Autowired
    private RandomFactsService randomFactsService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        String phoneNumber = update.getMessage().getContact().getPhoneNumber();

        logger.info("Received update to add user with chatId: {} and phone number: {}", chatId, phoneNumber);

        Phone phone = new Phone();
        phone.setNumber(phoneNumber);

        User user = new User();
        user.setTelegramId(update.getMessage().getFrom().getId());
        user.setName(update.getMessage().getFrom().getUserName());
        user.setPhone(phone);
        user.setChatId(chatId);
        user.setState(States.BASIC_STATE);

        logger.debug("User details: TelegramId: {}, Name: {}, ChatId: {}, State: {}",
                user.getTelegramId(), user.getName(), user.getChatId(), user.getState());

        if (userService.addUser(user)) {
            logger.info("User with chatId: {} added successfully", chatId);
            SendPhoto message = StartCommand.getStartCommand(randomFactsService);
            message.setChatId(String.valueOf(chatId));
            return new TelegramSendPhoto(message, String.valueOf(chatId));
        } else {
            logger.error("Error occurred while adding user with chatId: {}", chatId);
            SendMessage answer = new SendMessage(String.valueOf(chatId), "An error occurred.");
            return new TelegramSendMessage(answer, String.valueOf(chatId));
        }
    }
}