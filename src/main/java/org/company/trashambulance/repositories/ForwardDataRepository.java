package org.company.trashambulance.repositories;

import org.company.trashambulance.models.Form;
import org.company.trashambulance.models.ForwardData;
import org.company.trashambulance.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ForwardDataRepository extends CrudRepository<ForwardData, Long> {
    ForwardData findForwardDataByMessageId(Long messageId);
    ForwardData save(ForwardData forwardData);
    void deleteAllByCreationDateBefore(LocalDateTime dateTime);
    List<ForwardData> getForwardDataByCreationDateBefore(LocalDateTime dateTime);
}
