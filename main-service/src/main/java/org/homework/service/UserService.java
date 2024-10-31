package org.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.mail.MessagingException;
import org.homework.conventioin.result.Result;
import org.homework.pojo.dto.*;
import org.homework.pojo.po.User;

import java.io.IOException;
import java.io.InputStream;

/**
 * 针对表【admin】的数据库操作Service
 * 该接口提供了管理员账户相关的操作接口，包括登录、添加管理员和分页获取管理员列表
 * 继承自IService<Admin>以实现基础的CRUD操作
 *
 * @author zhanghaifeng
 * @createDate 2024-09-12 21:28:12
 */
public interface UserService extends IService<User> {

    /**
     * 管理员登录
     *
     * @param loginDto 登录请求数据，包含用户名和密码等信息
     * @return 登录结果，包含登录成功与否及可能的错误信息
     */
    Result login(LoginDto loginDto);

    /**
     * 添加管理员
     *
     * @param admin 管理员注册请求数据，包含管理员的基本信息
     * @return 操作结果，包含操作成功与否及可能的错误信息
     */
    Result addUser(AdminRegisterDTO admin);

    /**
     * 分页获取管理员列表
     *
     * @param adminPageDTO 请求参数，包含分页查询的条件和页码信息
     * @return 分页数据，包含管理员列表及总记录数等分页信息
     */
    Result getUserPage(AdminPageDTO adminPageDTO);

    /**
     * 修改个人消息
     */
    Result updateUser(AdminUpdateDTO adminUpdateDTO);

    /**
     * 删除管理员
     *
     * @param requestParam 管理员ID
     * @return 成功或失败的结果
     */
    Result deleteUser(String id);

    /**
     * 根据真实姓名模糊查询管理员
     *
     * @param requestParam 姓名
     * @return 管理员列表
     */
    Result fuzzyQueryUser(String searchText);

    /**
     * 设置用户角色
     * 此方法用于更新用户的角色信息，通常用于用户角色的变更操作
     *
     * @param userId 用户ID，标识唯一用户
     * @param roleId 角色ID，标识用户的新角色
     * @return 返回操作结果，包括操作状态和可能的错误信息
     */
    Result setUserRole(Long userId, Integer roleId);

    /**
     * 添加用户角色
     * 此方法用于给用户添加一个新的角色，通常用于扩展用户的权限
     *
     * @param userId 用户ID，标识唯一用户
     * @param roleId 角色ID，标识要添加给用户的新角色
     * @return 返回操作结果，包括操作状态和可能的错误信息
     */
    Result addUserRole(Long userId, Integer roleId);

    /**
     * 通过Excel文件导入用户信息
     * 此方法读取输入流中的Excel文件内容，并将用户数据导入到系统中
     * 它主要用于批量导入用户，提高数据录入效率和准确性
     *
     * @param file 包含用户信息的Excel文件的输入流
     * @return 返回导入操作的结果，包括是否成功和相关提示信息
     * @throws IOException 如果文件读取过程中发生错误
     */
    Result importUserByExcel(InputStream file) throws IOException;

    /**
     * 提交bug报告
     * <p>
     * 该方法接收一个BugReportDto对象作为参数，其中包含了关于bug的详细信息
     * 它负责将这些信息处理并可能存储到数据库或通过其他方式进行记录
     *
     * @param bugReport 包含了用户提交的bug报告详细信息的DTO对象
     * @return 返回一个Result对象，表示操作的结果，包括是否成功、错误信息等
     * @throws Exception 如果在处理bug报告过程中遇到任何异常或错误，该方法会抛出一个异常
     */
    Result bugReport(BugReportDto bugReport) throws Exception;

    /**
     * 提交建议并上传
     * 该方法接收用户的建议和联系方式作为参数，处理并可能将这些信息记录或发送给相关人员
     *
     * @param suggestion     用户的建议内容
     * @param contactDetails 用户的联系方式
     * @return 返回一个Result对象，表示操作的结果，包括是否成功、错误信息等
     * @throws MessagingException 如果在处理或发送建议过程中遇到错误
     */
    Result uploadSuggestion(String suggestion, String contactDetails) throws MessagingException;

}
