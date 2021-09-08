package com.ct.myim.framework.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        WebMvcConfigurer configurer = new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {

            }

            //注册拦截器
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                //静态资源在springboot2.0以前已经做好映射，不用管
                //  /**指任意范围都通过拦截
                registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/user/registerUser","/login","/file/getFile");
        //       asserts为resources下static下的文件夹，webjars则是maven导入的一些前端框架
            }
            
            @Bean
            public HttpMessageConverter<String> responseBodyStringConverter() {
                StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
                return converter;
            }

            /**
             * 修改StringHttpMessageConverter默认配置
             * @param converters
             */
            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters){
                converters.add(responseBodyStringConverter());
            }

        };
        return configurer;
    }

}
