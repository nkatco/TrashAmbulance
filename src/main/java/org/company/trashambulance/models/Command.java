package org.company.trashambulance.models;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    TelegramMessage apply(Update update);
}
