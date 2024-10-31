package org.homework.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPermissionDto {

    private String name;

    private String description;

    /**
     * 创建权限的同时，赋予给以下角色
     */
    private String roleIdListStr;

}
