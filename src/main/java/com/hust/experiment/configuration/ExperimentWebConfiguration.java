package com.hust.experiment.configuration;


import com.hust.experiment.interceptor.LoginRequiredInterceptor;
import com.hust.experiment.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class ExperimentWebConfiguration extends WebMvcConfigurerAdapter{
    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns("/loginIndex","/index",
                "/registerIndex","/register","/uploadFile","/getFile","/importExcel");
        super.addInterceptors(registry);
    }
}
