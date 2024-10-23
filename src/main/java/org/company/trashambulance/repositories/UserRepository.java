package org.company.trashambulance.repositories;

import org.company.trashambulance.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByTelegramId(long telegramId);
    User save(User user);
    User findUserById(String id);
}
