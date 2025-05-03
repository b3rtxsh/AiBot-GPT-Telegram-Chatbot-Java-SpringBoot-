package com.telegram.bot.repository;

import com.telegram.bot.entity.CreateTranscriptionRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreateTranscriptionRequestRepository extends
        JpaRepository<CreateTranscriptionRequestEntity, Long> {
}