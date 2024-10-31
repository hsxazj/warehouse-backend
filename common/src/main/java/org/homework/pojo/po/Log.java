package org.homework.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Log {

    @MongoId
    private String id;

    private String serverName;

    private String requestPath;

    private String requestMethod;

    private String ip;

    private LocalDateTime requestTime;

    private String requestArgs;

    private String responseCode;

}
