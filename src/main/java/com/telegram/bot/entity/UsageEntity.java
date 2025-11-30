package com.telegram.bot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_completion_usage")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private ChatCompletionRequestEntity request;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "prompt_tokens")
    private Integer promptTokens;

    @Column(name = "completion_tokens")
    private Integer completionTokens;

    @Column(name = "total_tokens")
    private Integer totalTokens;

    @Column(name = "prompt_price_per_1k", precision = 8, scale = 6)
    private BigDecimal promptPricePer1k;

    @Column(name = "completion_price_per_1k", precision = 8, scale = 6)
    private BigDecimal completionPricePer1k;

    @Column(name = "total_cost", precision = 10, scale = 6)
    private BigDecimal totalCost;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public void calculateTotalCost() {
        if (promptTokens == null || completionTokens == null
                || promptPricePer1k == null || completionPricePer1k == null) {
            totalCost = BigDecimal.ZERO;
            return;
        }

        BigDecimal promptCost = BigDecimal
                .valueOf(promptTokens)
                .divide(BigDecimal.valueOf(1000), 6, BigDecimal.ROUND_HALF_UP)
                .multiply(promptPricePer1k);

        BigDecimal completionCost = BigDecimal
                .valueOf(completionTokens)
                .divide(BigDecimal.valueOf(1000), 6, BigDecimal.ROUND_HALF_UP)
                .multiply(completionPricePer1k);

        totalCost = promptCost.add(completionCost);
    }
}
