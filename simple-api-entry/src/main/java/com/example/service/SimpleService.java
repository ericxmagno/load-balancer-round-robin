package com.example.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.logging.log4j.CloseableThreadContext.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.configuration.RestTemplateConfiguration;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

@Service
public class SimpleService {

	@Autowired
    private DiscoveryClient discoveryClient;
	
	@Autowired
	private RestTemplate restTemplate;
	
	int PING_TIMEOUT = 1000;
	
	private TreeSet<String> availableUriSet;
	
	private HashMap<String, ServiceInstance> serviceMap;
	
	private String requestUri;
	
	int requestCounter;
	
	public SimpleService() {
		availableUriSet = new TreeSet<String>();
		serviceMap = new HashMap<>();
	}
	
	public String sendRequest(String jsonString) throws Exception {
		System.out.println("Dispatching Request");
		do {
			getNextUri();
			requestCounter++;
			if(requestCounter == availableUriSet.size()) {
				throw new Exception ("APIs are offline.");
			}
		} while(isOnline() == false); 
		requestCounter = 0;
		HttpEntity<String> entity = new HttpEntity<String>(jsonString);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(requestUri + "/returnBody", entity, String.class);  
		return responseEntity.getBody();
	}
	
	public void getNextUri() throws Exception {
		getServices();
		if(requestUri == null || availableUriSet.last().equals(requestUri)) {
			requestUri = availableUriSet.first();
		} else {
			requestUri = availableUriSet.higher(requestUri);
		}
		System.out.println("URI set to " + requestUri);
	}
	
	public void getServices() throws Exception {
	    List<String> services = this.discoveryClient.getServices();
	    System.out.println("Found " + services.size() + " services.");
	    if(services.size() == 0) {
	    	throw new Exception("No services found.");
	    }
	    services.forEach(serviceName -> {
	        this.discoveryClient.getInstances(serviceName).forEach(instance ->{
	        	if(instance.getServiceId().equals("ROUND-ROBIN-ENTRY")){
	        		availableUriSet.add(instance.getUri().toString());
	        		if(!serviceMap.containsKey(instance.getUri().toString())) {
	        			serviceMap.put(instance.getUri().toString(), instance);
	        		}
	        	}
	        });
	    });
	}
	
    public boolean isOnline() {
        try (Socket socket = new Socket()) {
        	ServiceInstance instance = serviceMap.get(requestUri);
            socket.connect(new InetSocketAddress(instance.getHost(), instance.getPort()), PING_TIMEOUT);
            System.out.println(requestUri + " is online.");
            return true;
        } catch (IOException e) {
        	System.out.println(requestUri + " is offline.");
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }
	
}
