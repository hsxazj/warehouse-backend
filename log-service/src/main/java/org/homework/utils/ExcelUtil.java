package org.homework.utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;

/**
 * 功能描述：
 *
 * @Author：yid
 * @Date：2022/4/24 21:46
 */
public class ExcelUtil {

    private ExcelUtil() {
    }

    public static void export(Workbook wb, String name, HttpServletResponse res) {

        BufferedInputStream bis = null;
        try (ServletOutputStream out = res.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(out);) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            wb.write(os);
            byte[] content = os.toByteArray();

            InputStream is = new ByteArrayInputStream(content);
            // 设置response参数，可以打开下载页面
            res.reset();
            res.setContentType("application/vnd.ms-excel;charset=utf-8");
            res.setHeader("Access-Control-Allow-Origin", "*");
            res.setHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes(), "iso-8859-1"));

            bis = new BufferedInputStream(is);
            byte[] buff = new byte[2048];
            int bytesRead;
            // Simple read/write loop.
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            // log.info(e.getMessage());
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                // log.info(e.getMessage());
            }
        }
    }
}
