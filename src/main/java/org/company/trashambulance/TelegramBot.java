package org.company.trashambulance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.trashambulance.callbacks.CheckMessageCallback;
import org.company.trashambulance.commands.CommandsHandler;
import org.company.trashambulance.configs.BotConfig;
import org.company.trashambulance.handlers.*;
import org.company.trashambulance.models.TelegramMessage;
import org.company.trashambulance.models.TelegramSendForward;
import org.company.trashambulance.models.TelegramSendMessage;
import org.company.trashambulance.models.TelegramSendPhoto;
import org.company.trashambulance.services.UserService;
import org.company.trashambulance.states.StateData;
import org.company.trashambulance.states.States;
import org.company.trashambulance.utils.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    public final BotConfig botProperties;

    @Autowired
    public UserService userService;
    @Autowired
    public StateData stateData;
    @Value("${bot.chat.id}")
    String privateChatId;

    public final CallbacksHandler callbacksHandler;
    public final PhoneHandler phoneHandler;
    public final PhotoHandler photoHandler;
    public final CommandsHandler commandsHandler;
    public final TextHandler textHandler;
    public final ChatHandler chatHandler;

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        String chatId = null;
        long userId = 0;
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId().toString();
            userId = update.getMessage().getFrom().getId();
            if (chatId != null && userId != 0 && !update.getMessage().hasContact()) {
                if(update.getMessage().getChatId() != Long.parseLong(privateChatId)) {
                    checkMessage(sendMessage, chatId, userId, () -> {
                        if (update.hasMessage() && update.getMessage().hasText()) {
                            if (update.getMessage().getText().startsWith("/")) {
                                sendMessage(commandsHandler.handleCommands(update));
                            } else {
                                sendMessage(textHandler.handleCommands(update));
                            }
                        } else if (update.hasCallbackQuery()) {
                            sendMessage(callbacksHandler.handleCallbacks(update));
                        } else if (update.hasMessage() && update.getMessage().hasContact()) {
                            sendMessage(phoneHandler.handlePhone(update));
                        }
                    });
                } else {
                    if (update.hasMessage()
                            && update.getMessage().hasText()
                            && update.getMessage().getReplyToMessage() != null) {
                        sendMessage(chatHandler.handleCommands(update));
                    }
                }
            } else if (update.getMessage().hasContact()) {
                sendMessage(phoneHandler.handlePhone(update));
            } else {
                System.out.println("Отсутствуют UserId и TelegramId");
            }
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            userId = update.getCallbackQuery().getFrom().getId();
            if (chatId != null && userId != 0) {
                String finalChatId = chatId;
                checkMessage(sendMessage, chatId, userId, () -> {
                    if (update.hasMessage() && update.getMessage().hasText()) {
                        if (update.getMessage().getText().startsWith("/")) {
                            sendMessage(commandsHandler.handleCommands(update));
                        } else {
                            sendMessage(new TelegramSendMessage(new SendMessage(finalChatId, Consts.CANT_UNDERSTAND), finalChatId));
                        }
                    } else if (update.hasCallbackQuery()) {
                        sendMessage(callbacksHandler.handleCallbacks(update));
                    } else if (update.hasMessage() && update.getMessage().hasContact()) {
                        sendMessage(phoneHandler.handlePhone(update));
                    }
                });
            } else {
                System.out.println("Отсутствуют UserId и TelegramId");
            }
        } else if (update.hasMessage() && update.getMessage().hasContact()) {
            chatId = update.getMessage().getChatId().toString();
            userId = update.getMessage().getFrom().getId();
            if (chatId != null && userId != 0 && !update.getMessage().hasContact()) {
                String finalChatId1 = chatId;
                checkMessage(sendMessage, chatId, userId, () -> {
                    if (update.hasMessage() && update.getMessage().hasText()) {
                        if (update.getMessage().getText().startsWith("/")) {
                            sendMessage(commandsHandler.handleCommands(update));
                        } else {
                            sendMessage(new TelegramSendMessage(new SendMessage(finalChatId1, Consts.CANT_UNDERSTAND), finalChatId1));
                        }
                    } else if (update.hasCallbackQuery()) {
                        sendMessage(callbacksHandler.handleCallbacks(update));
                    } else if (update.hasMessage() && update.getMessage().hasContact()) {
                        sendMessage(phoneHandler.handlePhone(update));
                    }
                });
            } else if (update.getMessage().hasContact()) {
                sendMessage(phoneHandler.handlePhone(update));
            } else {
                System.out.println("Отсутствуют UserId и TelegramId");
            }
        } else if (update.hasMyChatMember()) {
            // Бот добавлен в канал
        } else if (update.hasPreCheckoutQuery()) {
            // Пречек оплата
        } else if (update.hasMessage() && update.getMessage().hasSuccessfulPayment()) {
            // Проверка оплаты
        } else if (update.hasMessage() && update.getMessage().hasDocument()) {
            chatId = update.getMessage().getChatId().toString();
            userId = update.getMessage().getFrom().getId();
            checkMessage(sendMessage, chatId, userId, () -> {
                sendMessage(photoHandler.handleCommands(update));
            });
        }
    }

    public Integer sendMessage(TelegramMessage telegramMessage) {
        try {
            Message message = null;
            if(telegramMessage instanceof TelegramSendMessage telegramSendMessage) {
                message = execute(telegramSendMessage.getSendMessage());
            } else if (telegramMessage instanceof TelegramSendPhoto telegramSendPhoto) {
                message = execute(telegramSendPhoto.getSendPhoto());
            } else if (telegramMessage instanceof TelegramSendForward telegramSendForward) {
                message = execute(telegramSendForward.getForwardMessage());
            }
            assert message != null;
            return message.getMessageId();
        } catch (Exception e) {
            System.out.println(e);
            return 0;
        }
    }

    private void checkMessage(SendMessage sendMessage, String chatId, long userId, CheckMessageCallback callback) {
        if(!userService.existsByTelegramId(userId)) {
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText("Чтобы взаимодействовать с сервисом, требуется регистрация.\n\nОтправьте свой номер, чтобы начать.");

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);

            List<KeyboardRow> keyboard = new ArrayList<>();

            KeyboardRow keyboardFirstRow = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();

            keyboardButton.setText("Отправить номер >");
            keyboardButton.setRequestContact(true);
            keyboardFirstRow.add(keyboardButton);

            keyboard.add(keyboardFirstRow);
            replyKeyboardMarkup.setKeyboard(keyboard);

            stateData.setCurrentState(States.ADD_PHONE_USER);

            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            sendMessage(new TelegramSendMessage(sendMessage, chatId));
        } else {
            callback.onVerificationUser();
        }
    }
}
