package com.myproject.queueSystem.domain.queue.dto;

import com.myproject.queueSystem.domain.queue.TYPE;
import jakarta.validation.constraints.NotBlank;

public record QueueDTO(
        TYPE type
){
}
