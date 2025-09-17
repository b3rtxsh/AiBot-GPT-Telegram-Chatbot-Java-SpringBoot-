package com.telegram.bot.repository;

import com.telegram.bot.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMessageRepository extends JpaRepository<Message, Long> {
}
