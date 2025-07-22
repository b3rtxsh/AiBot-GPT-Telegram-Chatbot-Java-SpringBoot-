package com.telegram.bot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "chat_completion_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatCompletionResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "chatCompletionResponse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChoiceEntity> choices;

    @Column(name = "message_content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "chat_completion_request_id", nullable = false)
    private ChatCompletionRequestEntity chatCompletionRequest;

}

