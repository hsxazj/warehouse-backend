package org.homework.pojo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
@AllArgsConstructor
@NonNull
public class AddRoleDto {

    private String name;

    private String description;

    /**
     * 创建角色的同时，为其添加以下权限
     */
    private String permissionIdListStr;

}
