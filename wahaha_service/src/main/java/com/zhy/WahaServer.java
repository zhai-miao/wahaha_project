package com.zhy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableJpaAuditing
@EntityScan(basePackages = {"com.zhy.pojo.**"})
public class WahaServer {
    public static void main(String[] args) {
        SpringApplication.run(WahaServer.class,args);
    }
    @RequestMapping("health")
    public String health(){
        System.out.println("==========Wahaha-SERVER  ok!==========");
        return "ok";
    }
}
