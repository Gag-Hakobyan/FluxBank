package com.fluxbank.service.impl;

import com.fluxbank.entity.User;
import com.fluxbank.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSchedulerService {
    private final UserService userService;

    @Scheduled(cron = "0 0 4 * * ?")
    public void CleanupUnverifiedUsersSchedule() {
        log.info("Starting scheduled cleanup of unverified users");
        List<User> unverifyUsers = userService.getAllByEnabled(false);
        log.info("Found {} unverified users in total", unverifyUsers.size());

        LocalDateTime dayAgo = LocalDateTime.now().minusHours(24);
        List<User> usersToBeDeleted = unverifyUsers.stream()
                .filter(user -> {
                    boolean isOld = user.getCreatedDate().isBefore(dayAgo);
                    if (isOld) {
                        log.debug("User {} ({}) will be deleted. Created: {}",
                                user.getName(), user.getEmail(), user.getCreatedDate());
                    }
                    return isOld;
                })
                .toList();

        log.info("Found {} users to delete (registered more than 24 hours ago)", usersToBeDeleted.size());

        if (!usersToBeDeleted.isEmpty()) {
            userService.deleteAll(usersToBeDeleted);
            log.info("Successfully deleted {} unverified users", usersToBeDeleted.size());
        } else {
            log.info("No users to delete");
        }
    }
}
