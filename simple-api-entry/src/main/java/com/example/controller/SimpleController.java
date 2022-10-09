package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.SimpleService;

@RestController
public class SimpleController {
	
	@Autowired
	private SimpleService service;
	
	@PostMapping("/returnThis")
    public String postBody(@RequestBody String jsonString) throws Exception {
		return service.sendRequest(jsonString);
        
    }
}
