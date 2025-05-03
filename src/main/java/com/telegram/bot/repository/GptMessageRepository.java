package com.telegram.bot.repository;

import com.telegram.bot.entity.GptMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GptMessageRepository extends JpaRepository<GptMessage, Long> {
}
