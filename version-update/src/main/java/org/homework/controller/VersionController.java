package org.homework.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.homework.conventioin.result.Result;
import org.homework.service.VersionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/version")
public class VersionController {
    private final VersionService versionService;

    /**
     * 检查版本
     *
     * @param currentVersion 当前版本
     * @return Result
     */
    @GetMapping("/checkVersion")
    Result checkVersion(@RequestParam("currentVersion") String currentVersion) {
        return versionService.checkVersion(currentVersion);
    }

    /**
     * TODO: 下载最新版本
     */
    @GetMapping("/downloadVersion")
    void downloadLatestVersion(HttpServletResponse response) {
        versionService.downloadLatestVersion(response);
    }

    /**
     * 更新新版本系统文件
     */
    @PostMapping("/uploadNewVersion")
    Result uploadNewVersion(@RequestParam("file") MultipartFile file,
                            @RequestParam("version") String version) {
        return versionService.uploadNewVersion(file, version);
    }

}
