package org.company.trashambulance.services;

import jakarta.transaction.Transactional;
import org.company.trashambulance.events.form.FormCreatedEvent;
import org.company.trashambulance.models.Form;
import org.company.trashambulance.models.ForwardData;
import org.company.trashambulance.repositories.FormsRepository;
import org.company.trashambulance.repositories.ForwardDataRepository;
import org.company.trashambulance.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ForwardDataService implements ForwardDataServiceImpl {

    @Autowired
    private ForwardDataRepository forwardDataRepository;

    @Transactional
    public void saveForwardData(ForwardData forwardData) {
        forwardDataRepository.save(forwardData);
    }
    public ForwardData getForwardDataByMessageId(Long messageId) {
        return forwardDataRepository.findForwardDataByMessageId(messageId);
    }

    @Transactional
    public void deleteExpiredForms() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusDays(15);
        forwardDataRepository.deleteAllByCreationDateBefore(expirationThreshold);
    }
}
