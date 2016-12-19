package com.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.services.model.MyRequest;

@SpringBootApplication
@RestController
@EnableEurekaClient
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    
    @PostMapping("/api/billing/addnos")
    public void addNos(@RequestBody MyRequest myrequest) {
    	System.out.println("Result is :" + myrequest.getX() + myrequest.getY());
   
}
    
}   
    @RefreshScope
    @RestController
    class MessageRestController {

        @Value("${message:Hello default}")
        private String message;

        @Value("${name:Gopal}")
        private String name;
        
        @RequestMapping("/api/billing/message/{name}/{city}")
        String getMessage(@PathVariable String name , @PathVariable String city) {
        	System.out.println("Inside Billing Service...");
            return this.message + ":" + name +":From " + city;
        }
        
        @RequestMapping("/api/billing/name")
        String getName() {
            return this.name;
        }
}
