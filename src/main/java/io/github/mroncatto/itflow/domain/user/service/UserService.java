package io.github.mroncatto.itflow.domain.user.service;

import io.github.mroncatto.itflow.application.model.AbstractService;
import io.github.mroncatto.itflow.application.service.MessageService;
import io.github.mroncatto.itflow.domain.commons.exception.BadRequestException;
import io.github.mroncatto.itflow.domain.email.service.EmailService;
import io.github.mroncatto.itflow.domain.user.dto.UserProfileRequestDto;
import io.github.mroncatto.itflow.domain.user.dto.UserRequestDto;
import io.github.mroncatto.itflow.domain.user.entity.User;
import io.github.mroncatto.itflow.domain.user.entity.UserRole;
import io.github.mroncatto.itflow.domain.user.exception.AlreadExistingUserByEmail;
import io.github.mroncatto.itflow.domain.user.exception.AlreadExistingUserByUsername;
import io.github.mroncatto.itflow.domain.user.exception.BadPasswordException;
import io.github.mroncatto.itflow.domain.user.exception.UserNotFoundException;
import io.github.mroncatto.itflow.domain.user.model.IUserService;
import io.github.mroncatto.itflow.infrastructure.persistence.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static io.github.mroncatto.itflow.domain.commons.helper.CompareHelper.distinct;
import static io.github.mroncatto.itflow.domain.commons.helper.GeneratorHelper.generateRandomAlphanumeric;
import static io.github.mroncatto.itflow.domain.commons.helper.ValidationHelper.isNull;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserService extends AbstractService implements IUserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MessageService messageService;

    @Override
    public User login(String username) {
        User user = this.userRepository.findUserByUsername(username);
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public Page<User> findAll(Specification<User> spec, Pageable pageable) {
        return this.userRepository.findAll(spec, pageable);
    }

    @Override
    public User findUserById(UUID id) throws UserNotFoundException {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(""));
    }

    @Override
    public User findUserByUsername(String username) {
        User user = this.userRepository.findUserByUsername(username);
        if (isNull(user))
            throw new UsernameNotFoundException("");
        log.debug(">>>FIND USER BY: {}", username);
        return user;
    }

    public User findUserByUsernameActiveOnly(String username) {
        User user = this.findUserByUsername(username);
        if (!user.isActive()) throw new UsernameNotFoundException(messageService.getMessage("badRequest.user_not_found_or_inactive"));
        log.debug(">>>FIND USER BY USERNAME ACTIVE ONLY: {}", username);
        return user;
    }

    @Override
    public User findUserByEmail(String email) throws UserNotFoundException {
        User user = this.userRepository.findUserByEmail(email);
        if (isNull(user))
            throw new UserNotFoundException("");
        log.debug(">>>FIND USER BY EMAIL: {}", email);
        return user;
    }

    @Override
    @Transactional
    public User save(UserRequestDto userRequestDto, BindingResult result) throws BadRequestException, AlreadExistingUserByUsername, AlreadExistingUserByEmail {
        validateResult(result);
        validateUniqueUsername(userRequestDto);
        validateUniqueEmail(userRequestDto.getEmail(), userRequestDto.getUsername());
        String randomPassword = generateRandomAlphanumeric(6, false);
        var user = new User();
        BeanUtils.copyProperties(userRequestDto, user);
        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setPasswordNonExpired(true);
        user.setActive(true);
        user.setJoinDate(LocalDateTime.now());
        this.emailService.welcome(user, randomPassword);
        log.debug(">>>CREATING USER: {}", userRequestDto);
        return this.userRepository.save(user);
    }

    @Override
    public User update(String username, UserRequestDto userRequestDto, BindingResult result) throws BadRequestException, AlreadExistingUserByEmail {
        validateResult(result);
        User user = this.findUserByUsername(username);
        validateUniqueEmail(userRequestDto.getEmail(), userRequestDto.getUsername());
        user.setFullName(userRequestDto.getFullName());
        user.setEmail(userRequestDto.getEmail());
        user.setStaff(userRequestDto.getStaff());
        log.debug(">>>UPDATING USER: {}", userRequestDto);
        return this.userRepository.save(user);
    }

    @Override
    public void delete(String username) {
        User user = this.findUserByUsername(username);
        user.setActive(false);
        log.debug(">>>DELETING USER BY: {}", username);
        this.userRepository.save(user);
    }

    @Override
    public User updateUserRoles(String username, List<UserRole> roles) {
        User user = this.findUserByUsernameActiveOnly(username);
        user.setRole(roles);
        this.userRepository.save(user);
        log.debug(">>>UPDATING USER {} WITH ROLES: {}", username, roles);
        return user;
    }

    @Override
    public User updateProfile(UserProfileRequestDto profileDto) throws AlreadExistingUserByEmail {
        String username = getContext().getAuthentication().getName();
        User user = this.findUserByUsername(username);
        validateUniqueEmail(profileDto.getEmail(), username);
        user.setEmail(profileDto.getEmail());
        user.setFullName(profileDto.getFullName());
        log.debug(">>>UPDATING USER PROFILE: {}", profileDto);
        return this.userRepository.save(user);
    }

    @Override
    public void updateUserPassword(String oldPassword, String newPassword) throws BadPasswordException {
        String username = getContext().getAuthentication().getName();
        User user = this.findUserByUsername(username);
        if (validateUserPassword(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setPasswordNonExpired(true);
            this.userRepository.save(user);
            log.debug(">>>UPDATING USER PASSWORD BY USER: {}", username);
        } else {
            throw new BadPasswordException("");
        }
    }

    @Override
    @Transactional
    public void resetUserPassword(String username) {
        User user = this.findUserByUsername(username);
        String randomPassword = generateRandomAlphanumeric(6, false);
        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setPasswordNonExpired(false);
        this.emailService.resetPassword(user, randomPassword);
        //TODO: Improve this method to use tokens
        log.debug(">>>RESET USER PASSWORD BY USER: {}", username);
        this.userRepository.save(user);
    }

    @Override
    public void lockUnlockUser(String username) {
        User user = this.findUserByUsername(username);
        if (!user.isActive()) throw new UsernameNotFoundException("");
        user.setNonLocked(!user.isNonLocked());
        log.debug(">>>LOCK USER ACCOUNT BY USER: {}", username);
        this.userRepository.save(user);
    }

    private void validateUniqueEmail(String email, String username) throws AlreadExistingUserByEmail {
        User anyuser = this.userRepository.findAllByEmail(email)
                .stream()
                .filter(User::isActive)
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(anyuser) && distinct(anyuser.getUsername(), username))
            throw new AlreadExistingUserByEmail("");
}

    private void validateUniqueUsername(UserRequestDto user) throws AlreadExistingUserByUsername {
        if (Objects.nonNull(this.userRepository.findUserByUsername(user.getUsername())))
            throw new AlreadExistingUserByUsername("");
    }


    private boolean validateUserPassword(String planePassword, String hashPassword) {
        return this.passwordEncoder.matches(planePassword, hashPassword);
    }
}
