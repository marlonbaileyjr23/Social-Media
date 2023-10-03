package com.example.lasya.SocialMediaApp.service;

import com.example.lasya.SocialMediaApp.bean.UserBean;
import com.example.lasya.SocialMediaApp.entity.User;
import com.example.lasya.SocialMediaApp.exception.UserNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.lasya.SocialMediaApp.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super();
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserBean createUser(UserBean userBean) {
        entityManager.getEntityManagerFactory().getCache().evictAll();
        User user = new User();
        user.setUserId(userBean.getUserId());
        return getUserBean(userBean, user);
    }

    @Override
    @Transactional
    public UserBean updateUser(UserBean userBean, int userId) {
        User user = null;
        Optional<User> optionalUser = userRepository.findById(userId);
        logger.debug("optionalUser:{} ", optionalUser);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Customer does not exist " + userId);
        }
        user = optionalUser.get();
        return getUserBean(userBean, user);
    }

    private UserBean getUserBean(UserBean userBean, User user) {
        user.setFirstName(userBean.getFirstName());
        user.setLastName(userBean.getLastName());
        user.setUserName(userBean.getUserName());
        user.setEmail(userBean.getEmail());
        user.setPassword(userBean.getPassword());
        user.setBio(userBean.getBio());
        user.setProfilePicture(userBean.getProfilePicture());
        user.setDateOfBirth(userBean.getDateOfBirth());
        user.setDateJoined(userBean.getDateJoined());
        user.setLastLogin(userBean.getLastLogin());
        user = userRepository.save(user);
        return getBeanFromEntity(user);
    }

    @Override
    public List<UserBean> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserBean> userBeans;
        userBeans = users.stream().map(this::getBeanFromEntity).collect(Collectors.toList());
        return userBeans;
    }

    @Override
    public UserBean getUserById(int id) {
        User user;
        Optional<User> userById = userRepository.findById(id);
        if (userById.isEmpty()) {
            throw new UserNotFoundException("User does not exist: " + id);
        }
        user = userById.get();
        return getBeanFromEntity(user);
    }

    @Override
    public boolean deleteUserById(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        logger.debug("optionalUser:{} ", optionalUser);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist: " + id);
        }
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public List<UserBean> searchUsers(String searchText) {
        String searchTextLowerCase = searchText.toLowerCase();
        List<User> users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                searchTextLowerCase, searchTextLowerCase, searchTextLowerCase, searchTextLowerCase
        );
        List<UserBean> userBeans = users.stream()
                .map(this::getBeanFromEntity)
                .collect(Collectors.toList());
        return userBeans;
    }

    @Override
    public UserBean signUpUser(UserBean userBean) {
        User user = getEntityFromBean(userBean);
        User savedUser = userRepository.save(user);
        return getBeanFromEntity(savedUser);
    }

    @Override
    public UserBean signInUser(String userName, String password) {
        User user = new User(userName, passwordEncoder.encode(password));
        // Step 1: Retrieve the user from the repository based on the username
        List<User> users = userRepository.findByUserName(userName);
        // Step 2: Check if a user is found
        if (!users.isEmpty()) {
            user = users.get(0);
            // Step 3: Verify that the user exists and the password matches
            if (passwordMatches(user, password)) {
                // Step 4: Return the corresponding UserBean
                return getBeanFromEntity(user);
            }
        }
        return null;
    }

    private boolean passwordMatches(User user, String password) {
        // Use the PasswordEncoder to verify the password
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public UserBean updatePasswordByEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            User updatedUser = userRepository.save(user);
            return getBeanFromEntity(updatedUser);
        }else {
            throw new UserNotFoundException("User does not exist: " + email);
        }
    }

    @Override
    public UserBean updateUsernameAndBio(int userId, String newUserName, String newBio) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUserName(newUserName);
            user.setBio(newBio);
            User updatedUser = userRepository.save(user);
            return getBeanFromEntity(updatedUser);
        }else {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
    }

    @Override
    public UserBean updateProfilePicture(int userId, byte[] newProfilePicture) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setProfilePicture(newProfilePicture);
            User updatedUser = userRepository.save(user);
            return getBeanFromEntity(updatedUser);
        } else {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
    }

    @Override
    public UserBean updateFirstNameAndLastName(int userId, String newFirstName, String newLastName) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFirstName(newFirstName);
            user.setLastName(newLastName);
            User updatedUser = userRepository.save(user);
            return getBeanFromEntity(updatedUser);
        }else {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
    }

    @Override
    public UserBean updateBio(int userId, String newBio) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setBio(newBio);
            User updatedUser = userRepository.save(user);
            return getBeanFromEntity(updatedUser);
        } else {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
    }

    @Override
    public Optional<User> findById(int userId) {
        return userRepository.findById(userId);
    }

    @Override
    public List<UserBean> getUsersWithPagination(int offset, int pageSize) {
        Page<User> users = userRepository.findAll(PageRequest.of(offset, pageSize));
        List<UserBean> userBeans = new ArrayList<>();
        userBeans = users.stream().map(this::getBeanFromEntity).collect(Collectors.toList());
        return userBeans;
    }

    public UserBean getBeanFromEntity(User user) {
        UserBean userBean = new UserBean();
        userBean.setUserId(user.getUserId());
        userBean.setFirstName(user.getFirstName());
        userBean.setLastName(user.getLastName());
        userBean.setUserName(user.getUserName());
        userBean.setEmail(user.getEmail());
        userBean.setPassword(user.getPassword());
        userBean.setBio(user.getBio());
        userBean.setDateOfBirth(user.getDateOfBirth());
        user.setDateJoined(user.getDateJoined());
        user.setLastLogin(user.getLastLogin());
        user.setProfilePicture(user.getProfilePicture());
        return userBean;
    }

    public User getEntityFromBean(UserBean userBean) {
        User user = new User();
        user.setUserId(userBean.getUserId());
        user.setFirstName(userBean.getFirstName());
        user.setLastName(userBean.getLastName());
        user.setUserName(userBean.getUserName());
        user.setEmail(userBean.getEmail());
        user.setPassword(userBean.getPassword());
        user.setBio(userBean.getBio());
        user.setDateOfBirth(userBean.getDateOfBirth());
        user.setDateJoined(userBean.getDateJoined());
        user.setLastLogin(userBean.getLastLogin());
        user.setProfilePicture(userBean.getProfilePicture());
        return user;
    }

}

