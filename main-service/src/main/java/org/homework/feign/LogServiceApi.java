package org.homework.feign;

import org.homework.conventioin.result.Result;
import org.homework.pojo.vo.GetApiLogByPageDto;
import org.homework.pojo.vo.LogVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "gateway")
public interface LogServiceApi {
    @GetMapping(value = "/log-service/log/getApiLogByPage")
    Result<List<LogVo>> getApiLogListByPage(@SpringQueryMap GetApiLogByPageDto getApiLogByPageDto);
}
