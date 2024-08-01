package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.BotState;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Bot {
    private BotState state;
}
