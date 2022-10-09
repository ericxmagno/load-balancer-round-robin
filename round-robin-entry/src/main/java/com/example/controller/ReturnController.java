package com.example.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReturnController {

	@PostMapping("/returnBody")
    public String postBody(@RequestBody String jsonString) throws Exception {
        if(isValid(jsonString)) { 	
        	return jsonString;
        } else {
        	return "Invalid JSON.";
        }
    }

	public boolean isValid(String json) {
	    try {
	        new JSONObject(json);
	    } catch (JSONException e) {
	        return false;
	    }
	    return true;
	}
}
