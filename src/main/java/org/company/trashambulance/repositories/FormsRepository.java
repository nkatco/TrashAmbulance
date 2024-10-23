package org.company.trashambulance.repositories;
import org.company.trashambulance.models.Form;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FormsRepository extends CrudRepository<Form, Long> {
    Form findFormByTelegramId(long telegramId);
    Form findFormById(String id);
    Form findFormByUserId(String id);
    List<Form> findAllByTelegramId(long id);
    List<Form> findAllByUserId(String id);
    Form save(Form Form);
    void deleteFormByTelegramId(long telegramId);
    List<Form> findAll();
    void deleteById(String id);
    void deleteAllByCreationDateBefore(LocalDateTime dateTime);
    List<Form> getFormsByCreationDateBefore(LocalDateTime dateTime);
}
