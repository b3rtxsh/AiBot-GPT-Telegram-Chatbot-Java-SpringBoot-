package com.telegram.bot.repository;

import com.telegram.bot.entity.TranscriptionResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranscriptionResponseRepository extends JpaRepository<TranscriptionResponseEntity, Long> {
}
