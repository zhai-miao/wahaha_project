package com.zhy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@RestController
@EnableJpaAuditing
@EntityScan(basePackages = {"com.zhy.pojo.**"})
public class SsoServer {
    public static void main(String[] args) {
        SpringApplication.run(SsoServer.class,args);
    }
    @RequestMapping("health")
    public String health(){
        System.out.println("==========SSO-SERVER  ok!==========");
        return "ok";
    }
}
