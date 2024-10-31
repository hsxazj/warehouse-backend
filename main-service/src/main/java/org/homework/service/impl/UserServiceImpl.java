package org.homework.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.homework.conventioin.exception.ServiceException;
import org.homework.conventioin.result.Result;
import org.homework.enums.OperationType;
import org.homework.feign.LogServiceApi;
import org.homework.mapper.UserMapper;
import org.homework.mapper.UserRoleMapper;
import org.homework.pojo.bo.LoginUser;
import org.homework.pojo.dto.*;
import org.homework.pojo.po.User;
import org.homework.pojo.po.UserRole;
import org.homework.pojo.vo.AdminPageVO;
import org.homework.pojo.vo.GetApiLogByPageDto;
import org.homework.pojo.vo.LogVo;
import org.homework.service.UserService;
import org.homework.utils.JwtUtil;
import org.homework.utils.MQUtil;
import org.homework.utils.MailUtil;
import org.homework.utils.RedisUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author zhanghaifeng
 * @description 针对表【admin】的数据库操作Service实现
 * @createDate 2024-09-12 21:28:12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private final AuthenticationManager authenticationManager;
    private final RedisUtil redisUtil;
    private final LogServiceApi logServiceApi;
    private final PasswordEncoder passwordEncoder;
    private final MQUtil mqUtil;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final MailUtil mailUtil;

    @Override
    public Result login(LoginDto loginDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getPhoneNumber(), loginDto.getPassword());
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (UsernameNotFoundException e) {
            return Result.fail(e.getMessage());
        } catch (AuthenticationException e) {
            return Result.fail("请检查账号密码");
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        User user = loginUser.getUser();
        Long id = user.getId();
        // 存入redis
        redisUtil.set(redisUtil.LOGIN_KEY + id, loginUser);
        HashMap<String, Object> map = new HashMap<>();
        map.put("adminId", id);
        String token = JwtUtil.sign(map);
        return Result.success(token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addUser(AdminRegisterDTO requestParam) {
        // 检查存在性
        User userExists = userMapper
                .selectOne(new LambdaQueryWrapper<User>().eq(User::getPhoneNumber, requestParam.getPhoneNumber()));
        if (userExists != null) {
            return Result.fail("该手机号已被使用");
        }
        User admin = BeanUtil.copyProperties(requestParam, User.class);

        admin.setPassword(passwordEncoder.encode(requestParam.getPassword()));
        boolean save = save(admin);
        if (!save) {
            return Result.fail("添加失败");
        }
        // 添加角色
        UserRole userRole = new UserRole();
        userRole.setUserId(admin.getId());
        userRole.setRoleId(requestParam.getRoleId());
        int insert = userRoleMapper.insert(userRole);
        if (insert != 1) {
            throw new ServiceException("添加角色失败");
        }
        mqUtil.sendOperationLogM(OperationType.PERSON, "新增用户：" + admin.getRealName());
        return Result.success();
    }

    @Override
    public Result getUserPage(AdminPageDTO adminPageDTO) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getDelFlag, 0);
        Page<User> tPage = Page.of(adminPageDTO.getCurrent(), adminPageDTO.getSize());
        Page<User> adminPage = baseMapper.selectPage(tPage, queryWrapper);
        IPage<AdminPageVO> resultPage = adminPage.convert(each -> BeanUtil.copyProperties(each, AdminPageVO.class));
        return Result.success(resultPage);
    }

    @Override
    public Result updateUser(AdminUpdateDTO adminUpdateDTO) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers
                .lambdaQuery(User.class)
                .eq(User::getPhoneNumber, adminUpdateDTO.getPhoneNumber())
                .eq(User::getDelFlag, 0);
        User hasUser = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(hasUser)) {
            Result.fail("该手机号不存在");
        }
        User build = User.builder()
                .address(StrUtil.isNotBlank(adminUpdateDTO.getAddress()) ? adminUpdateDTO.getAddress() : null)
                .birth(adminUpdateDTO.getBirth())
                .gender(adminUpdateDTO.getGender())
                .realName(adminUpdateDTO.getRealName())
                .password(StrUtil.isNotBlank(adminUpdateDTO.getPassword()) ? passwordEncoder.encode(adminUpdateDTO.getPassword()) : null)
                .build();
        int updateFlag = baseMapper.update(build, queryWrapper);
        mqUtil.sendOperationLogM(OperationType.PERSON, "修改用户：" + adminUpdateDTO.getRealName());
        return updateFlag == 1 ? Result.success("更新成功") : Result.fail("更新失败");
    }

    @Override
    public Result deleteUser(String id) {
        User user = baseMapper.selectById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        // TODO 逻辑删除
        LambdaUpdateWrapper<User> updateWrapper = Wrappers.lambdaUpdate(User.class).eq(User::getId, id)
                .eq(User::getDelFlag, 0)
                .set(User::getDelFlag, 1);
        int updateFlag = baseMapper.update(null, updateWrapper);
        if (updateFlag != 1) {
            return Result.fail("删除失败");
        }
        mqUtil.sendOperationLogM(OperationType.PERSON, "删除用户：" + user.getRealName());
        return Result.success();
    }

    @Override
    public Result fuzzyQueryUser(String searchText) {
        List<User> userList = userMapper.fuzzySelectByRealName(searchText);
        return Result.success(userList);

    }

    @Override
    public Result setUserRole(Long userId, Integer roleId) {
        LambdaUpdateWrapper<UserRole> updateWrapper = Wrappers.lambdaUpdate(UserRole.class).eq(UserRole::getUserId, userId)
                .set(UserRole::getRoleId, roleId);
        int updateFlag = userRoleMapper.update(null, updateWrapper);
        if (updateFlag != 1) {
            return Result.fail("修改失败");
        }
        redisUtil.delete(redisUtil.LOGIN_KEY + userId);
        mqUtil.sendOperationLogM(OperationType.PERSON, "设置用户：" + userId + "的角色为：" + roleId);
        return Result.success("修改成功");
    }

    @Override
    public Result addUserRole(Long userId, Integer roleId) {
        LambdaQueryWrapper<UserRole> wrapper = Wrappers.lambdaQuery(UserRole.class);
        wrapper.eq(UserRole::getUserId, userId);
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        int insertFlag = userRoleMapper.insert(userRole);
        mqUtil.sendOperationLogM(OperationType.PERSON, "为用户：" + userId + "添加角色：" + roleId);
        return insertFlag == 1 ? Result.success("添加成功") : Result.fail("添加失败");
    }

    @Override
    public Result importUserByExcel(InputStream file) throws IOException {
        List<User> userList = EasyExcel.read(file, User.class, new ReadListener<User>() {
            @Override
            public void invoke(User user, AnalysisContext analysisContext) {
                log.info("解析到一条数据:{}", user);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        }).excelType(ExcelTypeEnum.XLSX).sheet().headRowNumber(2).doReadSync();
        Date date = new Date();
        String encode = passwordEncoder.encode("123456");
        // 数据处理
        userList = userList.stream()
                .filter(e -> PhoneUtil.isMobile(e.getPhoneNumber())
                        && IdcardUtil.isValidCard(e.getIdentity())
                )
                .peek(e -> {
                    e.setCreateTime(date);
                    e.setPassword(encode);
                }).toList();
        if (CollectionUtils.isEmpty(userList)) {
            return Result.fail("无有效数据");
        }
        userMapper.insertIgnore(userList);
        List<UserRole> userRoleList = userList.stream().filter(e -> e.getId() != null).map(e -> {
            UserRole userRole = new UserRole();
            userRole.setRoleId(18);
            userRole.setUserId(e.getId());
            return userRole;
        }).toList();
        if (!CollectionUtils.isEmpty(userRoleList)) {
            userRoleMapper.insert(userRoleList);
        }
        mqUtil.sendOperationLogM(OperationType.PERSON, "使用excel导入用户");
        return Result.success();
    }

    @Override
    public Result bugReport(BugReportDto bugReport) throws Exception {
        GetApiLogByPageDto getApiLogByPageDto = new GetApiLogByPageDto();
        getApiLogByPageDto.setCurrentPage(1);
        getApiLogByPageDto.setSize(100);
        getApiLogByPageDto.setForExport(true);
        getApiLogByPageDto.setIsDesc(true);
        Date time = bugReport.getTime();
        // 获取前后2分钟
        Date startTime = DateUtil.offsetMinute(time, -2);
        Date endTime = DateUtil.offsetMinute(time, 2);
        getApiLogByPageDto.setStartTime(startTime);
        getApiLogByPageDto.setEndTime(endTime);
        // 获取api日志
        Result<List<LogVo>> apiLogListByPage = logServiceApi.getApiLogListByPage(getApiLogByPageDto);
        List<LogVo> logVoList = apiLogListByPage.getData();
        String apiLogPath = "BugReport" + UUID.fastUUID() + ".xlsx";
        EasyExcel
                .write(apiLogPath, LogVo.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .doWrite(logVoList);
        mailUtil.sendErrorLog(bugReport.getDescription(), apiLogPath, bugReport.getContactDetails());
        mqUtil.sendOperationLogM(OperationType.OTHER, "提交bug报告");
        return Result.success();
    }

    @Override
    public Result uploadSuggestion(String suggestion, String contactDetails) throws MessagingException {
        mailUtil.sendSuggestion(suggestion, contactDetails);
        mqUtil.sendOperationLogM(OperationType.OTHER, "提交意见反馈");
        return Result.success();
    }
}




