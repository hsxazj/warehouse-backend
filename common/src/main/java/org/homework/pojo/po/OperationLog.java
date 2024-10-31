package org.homework.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperationLog {

    @MongoId
    private String id;

    private String operationUser;

    private Long operationUserId;

    private LocalDateTime operationTime;

    private String description;

    private String type;

}
