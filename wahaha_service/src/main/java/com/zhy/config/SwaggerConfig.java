package com.zhy.config;


//import com.example.demoofswagger.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
//import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Autowired
    private Environment environment;

    @Bean
    public Docket getDocket(){

        Docket docket=new Docket(DocumentationType.SWAGGER_2);

        //配置接口的过滤start
        docket.select()
                .apis(RequestHandlerSelectors.basePackage("com.zhy.web"))//根据报名匹配接口的展示
                //.paths(PathSelectors.ant("/swagger/**"))//根据路径的正则去匹配
                .build();
        //根据包名配置接口的过滤end

        //配置忽略参数-start
        //docket.ignoredParameterTypes(String.class);
        //配置忽略参数-end

        //配置动态的显示接口文档start 大家注意一下SpringBoot的版本需要时2.1.0以后的版本
//        Profiles of = Profiles.of("dev", "test" );
//        boolean b = environment.acceptsProfiles(of);
//        docket.enable(b);
        //配置动态的显示接口文档end

        //配置API的分组-start  多个分组需要多个Docket实例
        docket.groupName("swagger1");
        //配置API的分组end

        //配置全局的参数start
        List<Parameter> parameterList=new ArrayList<>();
        Parameter parameter=new ParameterBuilder()
                .name("token")
                .parameterType("header")
                .description("请求令牌")
                .modelRef(new ModelRef("string"))
                .build();
        parameterList.add(parameter);
        docket.globalOperationParameters(parameterList);

        //配置全局的参数end

        docket.apiInfo(setApiInfo());
        return docket;
    }


    @Bean
    public Docket getDocket2(){

        Docket docket=new Docket(DocumentationType.SWAGGER_2);

        //配置接口的过滤start
        docket.select()
                .apis(RequestHandlerSelectors.basePackage("com.zhy.web"))//根据报名匹配接口的展示
                //.paths(PathSelectors.ant("/swagger2/**"))//根据路径的正则去匹配
                .build();

        //根据包名配置接口的过滤end

        //配置忽略参数-start
        //docket.ignoredParameterTypes(String.class,UserInfo.class);
        //配置忽略参数-end

        //配置动态的显示接口文档start 大家注意一下SpringBoot的版本需要时2.1.0以后的版本
        /*Profiles of = Profiles.of("dev", "test" );
        boolean b = environment.acceptsProfiles(of);
        docket.enable(b);*/
        //配置动态的显示接口文档end

        //配置API的分组
        docket.groupName("swagger2");

        docket.apiInfo(setApiInfo());
        return docket;
    }


    public ApiInfo setApiInfo(){

        Contact contact=new Contact("张三","http://www.baidu.com","5555@qq.com");
        ApiInfo apiInfo=new ApiInfo(
                "这是文档的标题",
                "这是文档信息的描述信息",
                "v-1.0",
                "http://www.baidu.com",
                contact,"监听信息",
                "http://www.baidu.com",
                new ArrayList<VendorExtension>()
                );

        return apiInfo;
    }

}
