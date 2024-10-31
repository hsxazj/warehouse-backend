package org.homework.service;

import jakarta.servlet.http.HttpServletResponse;
import org.homework.conventioin.result.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * 版本服务接口
 */
public interface VersionService {
    Result checkVersion(String currentVersion);

    void downloadLatestVersion(HttpServletResponse response);

    /**
     * 更新新版本
     *
     * @param file    上传的文件
     * @param version 版本号
     * @return 上传结果
     */
    Result uploadNewVersion(MultipartFile file, String version);
}
