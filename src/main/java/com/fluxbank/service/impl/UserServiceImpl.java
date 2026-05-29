package com.fluxbank.service.impl;

import com.fluxbank.dto.PagingRequestDto;
import com.fluxbank.dto.PagingResponseDto;
import com.fluxbank.dto.account.AccountResponseDto;
import com.fluxbank.dto.user.*;
import com.fluxbank.entity.QAccount;
import com.fluxbank.entity.QUser;
import com.fluxbank.entity.User;
import com.fluxbank.enums.AccountStatus;
import com.fluxbank.enums.UserRole;
import com.fluxbank.enums.UserStatus;
import com.fluxbank.exception.NotFoundException;
import com.fluxbank.mapper.AccountMapper;
import com.fluxbank.mapper.UserMapper;
import com.fluxbank.repository.UserRepository;
import com.fluxbank.service.AccountService;
import com.fluxbank.service.UserService;
import com.fluxbank.util.GenerateUtil;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MailService mailService;
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final GenerateUtil generateUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${upload.image.path}")
    private String imageDirectoryPath;

    @Override
    public UserResponseDto register(UserSaveDto saveDto) {
        User user = userMapper.toEntity(saveDto);
        user.setPassword(passwordEncoder.encode(saveDto.getPassword()));

        user.setVerifyToken(generateUtil.generate(6));

        mailService.sendVerificationMail(
                user.getEmail(),
                "Account Verification",
                String.format("Dear %s welcome you have successfully registered to our website, " +
                                "your verification code is %s",
                        user.getName(), user.getVerifyToken()));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findByIdAndRole(id, UserRole.USER)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public PagingResponseDto getAllByRole(PagingRequestDto pagingDto, UserFilterDto filterDto, UserRole role) {
        JPAQuery<User> query = new JPAQuery<>(entityManager);
        QUser qUser = QUser.user;

        query.from(qUser).where(qUser.role.eq(role));
        if (StringUtils.isNoneBlank(filterDto.getName())) {
            query.where(qUser.name.contains(filterDto.getName()));
        }
        if (StringUtils.isNoneBlank(filterDto.getSurname())) {
            query.where(qUser.surname.contains(filterDto.getSurname()));
        }
        if (StringUtils.isNoneBlank(filterDto.getEmail())) {
            query.where(qUser.email.contains(filterDto.getEmail()));
        }
        if (filterDto.getBirthDate() != null) {
            query.where(qUser.birthDate.eq(filterDto.getBirthDate()));
        }
        if (filterDto.getStatus() != null) {
            query.where(qUser.status.eq(filterDto.getStatus()));
        }
        if (filterDto.getCreatedFrom() != null) {
            query.where(qUser.createdDate.goe(filterDto.getCreatedFrom().atTime(LocalTime.MIN)));
        }
        if (filterDto.getCreatedTo() != null) {
            query.where(qUser.createdDate.loe(filterDto.getCreatedTo().atTime(LocalTime.MAX)));
        }
        if (filterDto.getAccountId() != null) {
            QAccount qAccount = QAccount.account;
            query.join(qAccount).on(qAccount.user.id.eq(qUser.id))
                    .where(qAccount.id.eq(filterDto.getAccountId()));
        }

        long total = query.fetchCount();
        query.offset((long) pagingDto.getPage() * pagingDto.getSize());
        query.limit(pagingDto.getSize());

        PathBuilder<Object> expression = new PathBuilder<>(User.class, "user");
        OrderSpecifier orderSpecifier = new OrderSpecifier(
                pagingDto.getOrderDirection().equalsIgnoreCase("ASC") ? Order.ASC : Order.DESC,
                expression.get(pagingDto.getOrderBy()));
        query.orderBy(orderSpecifier);

        List<User> users = query.fetch();


        return PagingResponseDto.builder()
                .data(userMapper.toDtoList(users))
                .total(total)
                .page(pagingDto.getPage())
                .size(pagingDto.getSize())
                .build();
    }

    @Override
    public List<User> getAllByEnabled(boolean enabled) {
        return userRepository.findAllByEnabled(enabled);
    }

    @Override
    public UserResponseDto getByVerifyToken(String token) {
        Optional<User> user = userRepository.findByVerifyToken(token);
        if (user.isEmpty()) {
            return null;
        }

        return userMapper.toDto(user.get());
    }

    @Override
    public void verify(UserResponseDto userResponseDto) {
        User user = getByEmail(userResponseDto.getEmail());
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);
        user.setVerifyToken(null);
        userRepository.save(user);

        accountService.createDefaultAccount(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteAll(List<User> users) {
        userRepository.deleteAll(users);
    }

    @Override
    public void uploadImage(User user, MultipartFile multipartFile) throws IOException {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String newImageName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            multipartFile.transferTo(new File(imageDirectoryPath, newImageName));

            String currentImageName = user.getImageName();
            user.setImageName(newImageName);
            userRepository.save(user);

            if (currentImageName != null) {
                File currentImage = new File(imageDirectoryPath, currentImageName);
                if (currentImage.exists()) {
                    currentImage.delete();
                }
            }
        } else {
            throw new IllegalArgumentException("Image file is required");
        }
    }

    @Override
    public byte[] getImage(String name) throws IOException {
        File image = new File(imageDirectoryPath, name);
        if (!image.exists()) {
            throw new IllegalArgumentException("Image not found: " + name);
        }
        return IOUtils.toByteArray(new FileInputStream(image));
    }

    @Override
    public UserResponseDto update(User user, UserUpdateDto updateDto) {
        updateDto.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        user.setName(updateDto.getName());
        user.setSurname(updateDto.getSurname());
        user.setPassword(updateDto.getPassword());
        user.setBirthDate(updateDto.getBirthDate());
        return userMapper.toDto(save(user));
    }

    @Override
    public void changeStatusDto(long id, UserChangeStatusDto changeStatusDto) {
        User user = getUserById(id);

        if (changeStatusDto.getStatus().equals(user.getStatus())) {
            throw new IllegalArgumentException("Status must be changed");
        }

        if (changeStatusDto.getStatus() != UserStatus.ACTIVE) {
            List<AccountResponseDto> accounts = accountService.getAllByUserEmail(user.getEmail());
            for (AccountResponseDto account : accounts) {
                account.setStatus(AccountStatus.BLOCKED);
                accountService.save(accountMapper.toEntity(account));
            }
        }

        user.setStatus(changeStatusDto.getStatus());
        user.setEnabled(changeStatusDto.getStatus() == UserStatus.ACTIVE);
        save(user);
    }
}
