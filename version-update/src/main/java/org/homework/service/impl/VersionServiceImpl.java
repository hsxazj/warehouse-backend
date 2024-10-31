package org.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.homework.conventioin.result.Result;
import org.homework.mapper.VersionMapper;
import org.homework.pojo.po.SystemVersion;
import org.homework.service.VersionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Objects;

@Service
public class VersionServiceImpl extends ServiceImpl<VersionMapper, SystemVersion> implements VersionService {
    private final String FILE_PATH = "F:/download/testUpload/";

    @Override
    public Result checkVersion(String currentVersion) {
        SystemVersion systemVersion = baseMapper.selectOne(new LambdaQueryWrapper<>());
        if (Objects.isNull(systemVersion)) {
            return Result.fail("系统版本信息不存在");
        }
        if (systemVersion.getVersion().equals(currentVersion)) {
            return Result.fail("当前版本已是最新");
        }

        return Result.success("有新版本发布，请前往下载页面下载最新版本");
    }

    @Override
    public void downloadLatestVersion(HttpServletResponse response) {
        // 获取最新版本信息
        SystemVersion systemVersion = baseMapper.selectOne(new LambdaQueryWrapper<>());
        String filePath = FILE_PATH + systemVersion.getFileName();

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filePath.substring(filePath.lastIndexOf('/') + 1) + "\"");
        // 写入文件到响应流
        try (InputStream inputStream = new FileInputStream(new File(filePath));
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result uploadNewVersion(MultipartFile file, String version) {
        if (file.isEmpty()) {
            return Result.fail("上传文件为空");
        }
        String originalFilename = file.getOriginalFilename();
        //获取文件类型
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        //生成新的文件名
        String newFileName = originalFilename.substring(0, originalFilename.lastIndexOf('.')) + "_" + version + fileExtension;
        //文件保存路径
        String filePath = FILE_PATH + newFileName;
        //保存文件
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("上传失败");
        }
        //更新数据库
        SystemVersion systemVersion = SystemVersion.builder().fileName(newFileName).version(version).build();

        int updateFlag = baseMapper.update(systemVersion, new LambdaQueryWrapper<SystemVersion>().eq(SystemVersion::getId, 1));
        if (updateFlag != 1) {
            throw new RuntimeException("数据更新失败");
        }
        return Result.success("上传成功");
    }


}
