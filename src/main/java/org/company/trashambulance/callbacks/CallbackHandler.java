package org.company.trashambulance.callbacks;

import org.company.trashambulance.models.TelegramMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {
    TelegramMessage apply(Update update);
}
