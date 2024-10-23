package org.company.trashambulance.events.form;

import org.company.trashambulance.models.Form;
import org.company.trashambulance.models.ForwardData;
import org.company.trashambulance.services.ForwardDataService;
import org.company.trashambulance.utils.TelegramBotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Component
public class FormEventListener {

    @Autowired
    private TelegramBotUtils telegramBotUtils;
    @Autowired
    private ForwardDataService forwardDataService;
    @Value("${bot.chat.id}")
    String chatId;

    @EventListener
    public void handleFormCreated(FormCreatedEvent event) {
        Form form = event.getForm();
        System.out.println("New Form created: " + form);
        int messageId = 0;

        if(form.getPhoto() != null) {
            InputFile file = new InputFile(new File(form.getPhoto()));
            messageId = telegramBotUtils.sendMessageForChannel(chatId, file,
                    "<b>ЗАЯВЛЕНИЕ " + form.getFormattedCreationDate() + "</b>" +
                    "\n" + form.getText() +
                    "\n\n<i>" + form.getAddress() + "</i>" +
                    "\n" + form.getUser().getPhone().getNumber(),
                    form.getMessageId(),
                    form.getChatId()
                    );
        } else {
            InputFile file = new InputFile(new File("src/main/resources/images/banner.png"));
            messageId = telegramBotUtils.sendMessageForChannel(chatId, file,
                    "<b>ЗАЯВЛЕНИЕ " + form.getFormattedCreationDate() + "</b>" +
                    "\n" + form.getText() +
                    "\n\n<i>" + form.getAddress() + "</i>" +
                    "\n" + form.getUser().getPhone().getNumber(),
                    form.getMessageId(),
                    form.getChatId()
                    );
        }
        ForwardData forwardData = new ForwardData();
        forwardData.setChatId(form.getChatId());
        forwardData.setMessageId((long) messageId);
        forwardData.setForm(form);
        forwardDataService.saveForwardData(forwardData);
    }
}
