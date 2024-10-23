package org.company.trashambulance.models;

import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

@Data
public class TelegramSendForward implements TelegramMessage {

    private ForwardMessage forwardMessage;
    private String chatId;

    public TelegramSendForward(ForwardMessage forwardMessage, String chatId) {
        this.forwardMessage = forwardMessage;
        forwardMessage.setChatId(chatId);
        this.chatId = chatId;
    }

    @Override
    public String getChatId() {
        return chatId;
    }

    @Override
    public void setChatId(String chatId) {
        this.chatId = chatId;
        if (forwardMessage != null) {
            this.forwardMessage.setChatId(chatId);
        }
    }
}
