package com.telegram.bot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "choices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "gpt_message_id", nullable = false)
    private Message message;

    @ManyToOne
    @JoinColumn(name = "chat_completion_response_id", nullable = false)
    private ChatCompletionResponseEntity chatCompletionResponse;
}
