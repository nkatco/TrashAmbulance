package org.company.trashambulance.services;

import jakarta.transaction.Transactional;
import org.company.trashambulance.events.form.FormCreatedEvent;
import org.company.trashambulance.utils.FileUtils;
import org.company.trashambulance.models.Form;
import org.company.trashambulance.repositories.FormsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class FormService implements FormServiceImpl {
    @Autowired
    private FormsRepository formRepository;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public boolean addForm(Form form) {
        formRepository.save(form);
        return existsByTelegramId(form.getTelegramId());
    }

    public Form getFormById(String id) {
        return formRepository.findFormById(id);
    }
    public List<Form> getAllFormsByTelegramId(long id) {
        return formRepository.findAllByTelegramId(id);
    }

    @Transactional
    public void saveForm(Form form) {
        formRepository.save(form);
        eventPublisher.publishEvent(new FormCreatedEvent(this, getFormById(form.getId())));
    }

    public Form getFormByTelegramId(long telegramId) {
        return formRepository.findFormByTelegramId(telegramId);
    }
    public Form getFormByUserId(String id) {
        return formRepository.findFormByUserId(id);
    }
    public List<Form> getFormsByUserId(String id) {
        return formRepository.findAllByUserId(id);
    }
    @Transactional
    public void removeFormByTelegramId(long telegramId) {
        formRepository.deleteFormByTelegramId(telegramId);
    }

    public List<Form> getAll() {
        return formRepository.findAll();
    }

    @Override
    public boolean existsByTelegramId(long telegramId) {
        if(getFormByTelegramId(telegramId) != null) {
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteExpiredForms() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusDays(15);
        List<Form> list = formRepository.getFormsByCreationDateBefore(expirationThreshold);
        for(Form form : list) {
            if(form.getPhoto() != null) {
                fileUtils.removeFileByPath(form.getPhoto());
            }
            formRepository.deleteById(form.getId());
        }
    }
}
