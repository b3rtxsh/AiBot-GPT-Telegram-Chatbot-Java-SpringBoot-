package com.telegram.bot.repository;

import com.telegram.bot.entity.UsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageRepository extends
        JpaRepository<UsageEntity, Long> {
}