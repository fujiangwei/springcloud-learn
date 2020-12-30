package com.springcloud.learn.converter;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 封装转换器, 添加更多类型的支持
 * @author
 * @date
 */
public class WxMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    public WxMappingJackson2HttpMessageConverter() {
        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        // 添加text/html类型的支持
        mediaTypes.add(MediaType.TEXT_HTML);
        // 添加text/plain类型的支持.微信接口会用到
        mediaTypes.add(MediaType.TEXT_PLAIN);
        setSupportedMediaTypes(mediaTypes);
    }
}