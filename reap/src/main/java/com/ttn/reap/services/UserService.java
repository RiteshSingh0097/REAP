package com.ttn.reap.services;

import com.ttn.reap.entities.Role;
import com.ttn.reap.entities.User;
import com.ttn.reap.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User save(User user) {
        User userToSave = setBadges(user);
        userToSave.setPoints(calculatePoints(userToSave));
        userToSave.setFullName(user.getFirstName() + " " + user.getLastName());
        return userRepository.save(userToSave);
    }

    public List<User> getUserList() {
        return userRepository.findAll();
    }

    public Optional<User> getUser(Integer id) {
        return userRepository.findById(id);
    }

    public User findUserById(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.get();
    }

    User setBadges(User user) {
        if (user.getRoleSet().contains(Role.PRACTICE_HEAD)) {
            user.setGoldShareable(9);
            user.setSilverShareable(6);
            user.setBronzeShareable(3);
        } else if (user.getRoleSet().contains(Role.SUPERVISOR)) {
            user.setGoldShareable(6);
            user.setSilverShareable(3);
            user.setBronzeShareable(2);
        } else {
            user.setGoldShareable(3);
            user.setSilverShareable(2);
            user.setBronzeShareable(1);
        }
        return user;
    }

    public Integer calculatePoints(User user) {
        Integer points;
        points = user.getGoldRedeemable() * 30
                + user.getSilverRedeemable() * 20
                + user.getBronzeRedeemable() * 10;
        return points;
    }

    public User getUserByFullName(String fullName) {
        return userRepository.findByFullName(fullName);
    }


    // only active user can able to login
    public Optional<User> findUserByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPasswordAndActive(email, password, true);
    }

    public List<User> findUserByFullNamePattern(String fullNamePattern) {
        return userRepository.findByFullNameLike(fullNamePattern);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void adminEditUser(User user) {
        User userToSave = setBadges(user);
        userToSave.setPoints(calculatePoints(userToSave));
        userRepository.save(userToSave);
    }


    public Set<Role> roleModifier(Set<Role> roleSet, String value, Role role) {
        if (value == null) {
            roleSet.remove(role);
        } else roleSet.add(role);
        return roleSet;
    }

    public void revokeUserBadge(User receiver,User sender, String badge) {
        if (badge.equals("gold")) {
            receiver.setGoldRedeemable(receiver.getGoldRedeemable() - 1);
            sender.setGoldShareable(sender.getGoldShareable()+1);
        } else if (badge.equals("silver")) {
            receiver.setSilverRedeemable(receiver.getSilverRedeemable() - 1);
            sender.setSilverShareable(sender.getSilverShareable()+1);
        } else {
            receiver.setBronzeRedeemable(receiver.getBronzeRedeemable() - 1);
            sender.setBronzeShareable(sender.getBronzeShareable()+1);
        }
        receiver.setPoints(calculatePoints(receiver));
        userRepository.save(receiver);
        userRepository.save(sender);
    }
}
