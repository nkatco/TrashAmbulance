package org.company.trashambulance.utils;

import lombok.extern.slf4j.Slf4j;
import org.company.trashambulance.TelegramBot;
import org.company.trashambulance.callbacks.CallbackType;
import org.company.trashambulance.models.TelegramSendForward;
import org.company.trashambulance.models.TelegramSendMessage;
import org.company.trashambulance.models.TelegramSendPhoto;
import org.company.trashambulance.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class TelegramBotUtils {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotUtils.class);
    private final TelegramBot telegramBot;
    @Value("${images.url.forms}")
    String pathToSavePhotoWatermarks;

    @Autowired
    public TelegramBotUtils(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public List<ChatMember> getChatAdministrators(Long chatId){
        List<ChatMember> chatAdministrators = Collections.emptyList();
        try {
            chatAdministrators = telegramBot.execute(new GetChatAdministrators(String.valueOf(chatId)));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return chatAdministrators;
    }

    public void sendMessageForUser(User user, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(user.getChatId());
        message.setText(text);
        telegramBot.sendMessage(new TelegramSendMessage(message, String.valueOf(user.getChatId())));
    }
    public void sendMessageForChat(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode(ParseMode.HTML);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var channelButton = new InlineKeyboardButton();

        rowInLine = new ArrayList<>();
        channelButton = new InlineKeyboardButton();
        channelButton.setText("Главное меню");

        channelButton.setCallbackData(CallbackType.START_BUTTON);
        rowInLine.add(channelButton);
        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        logger.info("Send message for {}", chatId);

        telegramBot.sendMessage(new TelegramSendMessage(message, chatId));
    }
    public int sendMessageForChannel(String chatId, InputFile file, String text, long messageId, long fromChatId) {
        SendPhoto message = new SendPhoto();
        message.setChatId(chatId);
        message.setCaption(text);
        message.setPhoto(file);
        message.setParseMode(ParseMode.HTML);

        logger.info("Send message for {}", chatId);

        int newMessageId = telegramBot.sendMessage(new TelegramSendPhoto(message, chatId));

        sendForwardedMessageForChannel(chatId, messageId, fromChatId);
        return newMessageId;
    }
    public void sendForwardedMessageForChannel(String chatId, long messageId, long fromChatId) {
        ForwardMessage message = new ForwardMessage();
        message.setChatId(chatId);
        message.setFromChatId(fromChatId);
        message.setMessageId(Integer.parseInt(String.valueOf(messageId)));
        logger.info("Forward message for {} from {}", chatId, fromChatId);
        telegramBot.sendMessage(new TelegramSendForward(message, chatId));
    }
    public String downloadPhoto(String fileId) {
        try {
            logger.info("Download photo with fileId {}", fileId);
            GetFile getFileRequest = new GetFile();
            getFileRequest.setFileId(fileId);
            File file = telegramBot.execute(getFileRequest);
            String filePath = file.getFilePath();

            if (!filePath.endsWith(".png") && !filePath.endsWith(".jpg")) {
                logger.error("Invalid file format: {}", filePath);
                return null;
            }

            java.io.File dir = new java.io.File(pathToSavePhotoWatermarks);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileUrl = "https://api.telegram.org/file/bot" + telegramBot.getBotToken() + "/" + filePath;

            String generatedFileName = generateFileName() + ".jpg";
            String destinationPath = Paths.get(pathToSavePhotoWatermarks, generatedFileName).toString();

            downloadFile(fileUrl, destinationPath);
            logger.info("Download photo with fileId {} successfully", fileId);
            return destinationPath;
        } catch (TelegramApiException e) {
            logger.error("Failed to download photo", e);
        }
        return null;
    }

    public String generateFileName() {
        return UUID.randomUUID().toString();
    }

    public void downloadFile(String fileUrl, String destinationFile) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            logger.error("Failed to download file", e);
        }
    }
}
