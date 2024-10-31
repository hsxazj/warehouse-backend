package org.homework.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author zhanghaifeng
 */
@Slf4j
public class COSUtil {

    // cos文档(https://cloud.tencent.com/document/product/436)

    // TODO 修改 SECRET_ID SECRET_KEY BUCKET_NAME
    // 当然 如果你想要操作的 bucket 不止一个 可以删掉 BUCKET_NAME 然后手动传参
    private static final String SECRET_ID = "example";
    private static final String SECRET_KEY = "example";

    private static final String BUCKET_NAME = "example";


    /**
     * 根据文件路径上传本地磁盘中的文件
     *
     * @param baseKey   cos上的 base路径
     * @param fileNames 不限数量的文件名
     */
    public static void putObject(String baseKey, String... fileNames) {
        COSCredentials cred = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
        ClientConfig clientConfig = new ClientConfig(new Region("ap-nanjing"));
        COSClient cosClient = new COSClient(cred, clientConfig);
        try {
            for (String fileName : fileNames) {
                File fileLocal = new File(fileName);
                String key = baseKey + "/" + fileName;
                PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, fileLocal);
                PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
                log.info("文件成功已上传至COS，桶:{},key:{}", BUCKET_NAME, key);
            }
        } catch (CosClientException e) {
            throw new RuntimeException(e);
        } finally {
            cosClient.shutdown();
        }
    }

    /**
     * 分块上传
     *
     * @param baseKey base路径
     * @param files   不限数量的MultipartFile对象
     */
    public void MultipartUpload(String baseKey, Pattern verifyType, MultipartFile... files) throws IOException {
        COSCredentials cred = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
        ClientConfig clientConfig = new ClientConfig(new Region("ap-nanjing"));
        COSClient cosClient = new COSClient(cred, clientConfig);

        // 由于百度接口限制，不可以开启多线程判断

        try {
            for (MultipartFile file : files) {

                String fileName = file.getOriginalFilename();

                if (!FileUtil.verifyFile(fileName, verifyType)) {
                    log.error("{}的文件类型是非法的,将跳过上传", fileName);
                    continue;
                }

                InputStream inputStream = file.getInputStream();
                long contentLength = file.getSize();
                long partSize = 10 * 1024 * 1024L; // 每部分大小，例如10MB
                long filePosition = 0;
                List<PartETag> partETags = new ArrayList<>();

                // 设置文件key
                String key = baseKey + "/" + fileName;

                // 发起请求获取uploadId
                InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(BUCKET_NAME, key);
                InitiateMultipartUploadResult result = cosClient.initiateMultipartUpload(request);
                String uploadId = result.getUploadId();

                // 分块
                for (long i = 0; filePosition < contentLength; i++) {
                    long curPartSize = Math.min(partSize, contentLength - filePosition);
                    UploadPartRequest uploadPartRequest = new UploadPartRequest();
                    uploadPartRequest.setBucketName(BUCKET_NAME);
                    uploadPartRequest.setKey(key);
                    uploadPartRequest.setUploadId(uploadId);
                    uploadPartRequest.setInputStream(inputStream);
                    uploadPartRequest.setPartSize(curPartSize);
                    uploadPartRequest.setPartNumber((int) (i + 1));
                    UploadPartResult uploadPartResult = cosClient.uploadPart(uploadPartRequest);
                    partETags.add(uploadPartResult.getPartETag());
                    filePosition += curPartSize;
                }
                // 上传
                CompleteMultipartUploadRequest completeMultipartUploadRequest
                        = new CompleteMultipartUploadRequest(BUCKET_NAME, key, uploadId, partETags);
                CompleteMultipartUploadResult completeResult
                        = cosClient.completeMultipartUpload(completeMultipartUploadRequest);
            }
        } finally {
            cosClient.shutdown();
        }
    }
}
