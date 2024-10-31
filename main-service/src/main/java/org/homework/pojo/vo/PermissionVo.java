package org.homework.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionVo {
    private Integer id;

    private String name;

    private String description;

    // 是否持有这个权限
    private boolean selected;
}
