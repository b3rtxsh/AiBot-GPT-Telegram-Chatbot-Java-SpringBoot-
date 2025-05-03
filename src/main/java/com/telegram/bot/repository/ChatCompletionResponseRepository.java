package com.telegram.bot.repository;

import com.telegram.bot.entity.ChatCompletionResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatCompletionResponseRepository extends JpaRepository<ChatCompletionResponseEntity, Long> {
}
