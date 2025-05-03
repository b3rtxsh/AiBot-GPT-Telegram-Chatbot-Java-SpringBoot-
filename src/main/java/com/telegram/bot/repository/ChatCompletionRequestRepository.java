package com.telegram.bot.repository;

import com.telegram.bot.entity.ChatCompletionRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatCompletionRequestRepository extends
        JpaRepository<ChatCompletionRequestEntity, Long> {
}
