package org.company.trashambulance.events.form;

import org.company.trashambulance.models.Form;
import org.company.trashambulance.models.ForwardData;
import org.company.trashambulance.services.BannerService;
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
    @Autowired
    private BannerService bannerService;
    @Value("${bot.chat.id}")
    String chatId;

    @EventListener
    public void handleFormCreated(FormCreatedEvent event) {
        Form form = event.getForm();
        System.out.println("New Form created: " + form);
        int messageId = 0;

        InputFile file;
        if(form.getPhoto() != null) {
            file = new InputFile(new File(form.getPhoto()));
        } else {
            file = new InputFile(bannerService.getImage());
        }
        messageId = telegramBotUtils.sendMessageForChannel(chatId, file,
                "<b>ЗАЯВЛЕНИЕ " + form.getFormattedCreationDate() + "</b>" +
                        "\n" + form.getText() + "\n\n<i>" +
                        form.getUser().getPhone().getNumber() + "</i>",
                form.getMessageId(),
                form.getChatId()
        );
        ForwardData forwardData = new ForwardData();
        forwardData.setChatId(form.getChatId());
        forwardData.setMessageId((long) messageId);
        forwardData.setForm(form);
        forwardDataService.saveForwardData(forwardData);
    }
}
