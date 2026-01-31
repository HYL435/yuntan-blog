package com.yuntan.api.client;

import com.yuntan.api.dto.AmapIpResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// url 指定第三方接口的地址，不走 Nacos
@FeignClient(name = "amap-client", url = "https://restapi.amap.com")
public interface AmapFeignClient {

    /**
     * 根据 IP 获取位置信息
     * 完整请求路径会拼接为：https://restapi.amap.com/v3/ip?key=xxx&ip=xxx
     */
    @GetMapping("/v3/ip")
    AmapIpResponseDTO getLocationByIp(
            @RequestParam("key") String key, 
            @RequestParam("ip") String ip
    );
}