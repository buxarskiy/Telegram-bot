package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersVote {
    private UUID id;
    private Long chatId;
    private String voteUserName;
    private String phoneNumber;
    private UUID schoolId;
    private LocalDateTime startTime;
//    private final LocalDateTime startVoteTime = LocalDateTime.now().minusHours(1);

//    private LocalDateTime time() {
//        LocalDateTime startVoteTime = LocalDateTime.now();
//
//        // Soat va sanani chiqarish
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
//        String formattedDateTime = startVoteTime.format(formatter);
//        return LocalDateTime.parse(formattedDateTime);
//    }


}
