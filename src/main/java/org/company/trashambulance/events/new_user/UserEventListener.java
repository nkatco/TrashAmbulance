package org.company.trashambulance.events.new_user;

import org.company.trashambulance.models.Form;
import org.company.trashambulance.models.ForwardData;
import org.company.trashambulance.models.User;
import org.company.trashambulance.services.ForwardDataService;
import org.company.trashambulance.utils.TelegramBotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Component
public class UserEventListener {

    @Autowired
    private TelegramBotUtils telegramBotUtils;

    @EventListener
    public void handleFormCreated(UserCreatedEvent event) {
        User user = event.getUser();
        System.out.println("New User created: " + user);

        telegramBotUtils.clearUserKeyboardMarkup(user);
    }
}
