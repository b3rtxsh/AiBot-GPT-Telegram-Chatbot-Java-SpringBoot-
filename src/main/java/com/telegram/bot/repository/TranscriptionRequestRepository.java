package com.telegram.bot.repository;

import com.telegram.bot.entity.TranscriptionRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptionRequestRepository extends JpaRepository<TranscriptionRequestEntity, Long> {
}
