package org.homework.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author zhanghaifeng
 */
@Slf4j
public class FileUtil {

    /**
     * 图片格式的正则表达式
     */
    public static final Pattern IMAGE_FORMAT_PATTERN = Pattern
            .compile(".*\\.(jpg|jpeg|png|gif|bmp|tiff|webp|svg|ico|heif|heic)$",
                    // 不区分大小写
                    Pattern.CASE_INSENSITIVE);

    private FileUtil() {
    }

    /**
     * 验证文件是否合法
     *
     * @param fileName   文件名
     * @param verityType 验证类型
     * @return 是否校验通过
     */
    public static boolean verifyFile(String fileName, Pattern verityType) {
        return StringUtils.hasText(fileName) && verityType.matcher(fileName).matches();
    }

    /**
     * 保存文件到本地
     *
     * @param files          文件数组
     * @param fileUploadPath 保存的位置
     * @param verityType     验证类型
     */
    public static void saveToLocal(MultipartFile[] files, String fileUploadPath, Pattern verityType) throws IOException {

        for (MultipartFile file : files) {
            // 获取文件原始名称
            String originalFilename = file.getOriginalFilename();

            // 对文件进行类型校验
            if (StringUtils.hasText(originalFilename) && verityType.matcher(originalFilename).matches()) {
                // 获取文件的类型
                String type = cn.hutool.core.io.FileUtil.extName(originalFilename);
                log.info("文件类型是：" + type);

                // 获取文件
                File uploadParentFile = new File(fileUploadPath);
                // 判断文件目录是否存在
                if (!uploadParentFile.exists()) {
                    // 如果不存在就创建文件夹
                    uploadParentFile.mkdirs();
                }
                File uploadFile = new File(fileUploadPath + originalFilename);
                // 将临时文件转存到指定磁盘位置
                file.transferTo(uploadFile);
            }

        }
    }
}
