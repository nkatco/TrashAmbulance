package org.company.trashambulance.events.form;

import org.company.trashambulance.models.Form;
import org.springframework.context.ApplicationEvent;

public class FormCreatedEvent extends ApplicationEvent {
    private final Form form;

    public FormCreatedEvent(Object source, Form form) {
        super(source);
        this.form = form;
    }

    public Form getForm() {
        return form;
    }
}