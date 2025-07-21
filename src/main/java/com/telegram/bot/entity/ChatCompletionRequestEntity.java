package com.telegram.bot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_completion_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatCompletionRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ai_model", nullable = false)
    private String model;

    @OneToOne(mappedBy = "completion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private GptMessage messages;

    @ManyToOne
    @JoinColumn(name = "chat_history_id", nullable = false)
    @JsonIgnore
    private ChatHistory chatHistory;

}
