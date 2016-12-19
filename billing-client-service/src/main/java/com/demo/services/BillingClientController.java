package com.demo.services;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class BillingClientController {

	@Autowired
	RestTemplate loadBalancedRestTemplate;
	
	@Autowired
    private LoadBalancerClient loadBalancer;
	
	
	//Using Load-Balanced RestTemplate_Eureka_Ribbon
	@GetMapping("/api/v1/billingclient/{id}/{id2}")
	public String getMessage(@PathVariable String id,@PathVariable String id2){
		ResponseEntity<String> response=loadBalancedRestTemplate.exchange("http://billing-service/api/billing/message/{id}/{id2}",HttpMethod.GET,null,String.class,id,id2);
		return response.getBody().toString();
	}

	//Using LoadBalancerClient_DefaultRestTemplate_Eureka_RibbonAPI
	@GetMapping("/api/v2/billingclient/{id}/{id2}")
	public String getMessage_Using_LoadBalancerClient_RibbonAPI(@PathVariable String id,@PathVariable String id2){
		RestTemplate restTemplate =new RestTemplate();
		ServiceInstance instance = loadBalancer.choose("billing-service");
	    URI billingUri = URI.create(String.format("http://%s:%s", instance.getHost(), instance.getPort()));
		ResponseEntity<String> response=restTemplate.exchange(billingUri + "/api/billing/message/{id}/{id2}",HttpMethod.GET,null,String.class,id,id2);
		return response.getBody().toString();
	}

	
	//Using Non-Balanced RestTemplate_DirectAccess
	@GetMapping("/api/v3/billingclient/{id}/{id2}")
	/*@HystrixCommand(fallbackMethod = "reliable")*/
	public String getMessageWithNon_LoadBalancedRestTemplate(@PathVariable String id,@PathVariable String id2){
		RestTemplate restTemplate =new RestTemplate();
		ResponseEntity<String> response=restTemplate.exchange("http://localhost:1080/api/billing/message/{id}/{id2}",HttpMethod.GET,null,String.class,id,id2);
		return response.getBody().toString();
	}
	
	
	
	//Using Gateway-ZUUL(RIBBON Load Balancer + Eureka)
	@GetMapping("/api/v4/billingclient/{id}/{id2}")
	public String getMessageWithNon_Load_BalancedRestTemplate_Ribbon(@PathVariable String id,@PathVariable String id2){
		ResponseEntity<String> response=loadBalancedRestTemplate.exchange("http://zuul-service/billing-service/api/billing/message/{id}/{id2}",HttpMethod.GET,null,String.class,id,id2);
		return response.getBody().toString();
	}
	
   public String reliable(@PathVariable String id,@PathVariable String id2){
	   return "all is well";
   }

	
	}
