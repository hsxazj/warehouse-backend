package org.homework;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import jakarta.annotation.Resource;
import org.homework.pojo.vo.LogVo;
import org.homework.pojo.vo.OperationLogVo;
import org.homework.utils.MongoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class SpringApplicationTest {

    @Resource
    private MongoUtil mongoUtil;

    @Resource
    private MongoTemplate mongoTemplate;


    @Test
    void testInsert() {
        List<LogVo> logVoList = mongoUtil.findAllAsList("api_log", LogVo.class);
        // 使用easyexcel导出
        EasyExcel.write("test.xlsx", LogVo.class).excelType(ExcelTypeEnum.XLSX).sheet("sheet1").doWrite(logVoList);

    }

    @Test
    void testQ() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "operationTime"));
        LocalDateTime localDateTime = LocalDateTime.of(2024, 10, 27, 14, 0, 0);
        LocalDateTime endLocalDateTime = LocalDateTime.of(2024, 10, 27, 14, 15, 0);
        query.addCriteria(Criteria.where("operationTime").gte(localDateTime).lte(endLocalDateTime));
        List<OperationLogVo> operationLog = mongoUtil.findList("operation_log",
                query,
                OperationLogVo.class);
        for (OperationLogVo operationLogVo : operationLog) {
            System.out.printf("\n");
            System.out.printf(operationLogVo.toString());
        }
    }

}
