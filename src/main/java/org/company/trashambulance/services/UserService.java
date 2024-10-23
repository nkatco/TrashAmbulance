package org.company.trashambulance.services;

import jakarta.transaction.Transactional;
import org.company.trashambulance.models.User;
import org.company.trashambulance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceImpl {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public boolean existsByTelegramId(long id) {
        User user = userRepository.findUserByTelegramId(id);
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }
    @Transactional
    public boolean addUser(User user) {
        userRepository.save(user);
        return existsByTelegramId(user.getTelegramId());
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User getUserByTelegramId(long telegramId) {
        return userRepository.findUserByTelegramId(telegramId);
    }
    public User getUserById(String id) {
        return userRepository.findUserById(id);
    }
}
