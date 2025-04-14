package com.example.springBootLearn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A controller to demonstrate hot swapping functionality.
 * You can modify the methods and templates while the application is running
 * to see hot swapping in action.
 */
@Controller
@RequestMapping("/hotswap-demo")
public class HotSwapDemoController {

    /**
     * REST endpoint that returns a simple text message.
     * Modify this message to test hot swapping with Java code.
     */
    @GetMapping("/api")
    @ResponseBody
    public String hello() {
        // Try changing this message and saving the file while the application is running
        // You should see the changes take effect without restarting the server
        return "Hello from Hot Swap Demo!  Change this message to test hot swapping.";
    }
    
    /**
     * Serves the Thymeleaf template for hot swap demonstration.
     * Modify the hotswap-demo.html template to test hot swapping with templates.
     */
    @GetMapping
    public String demoPage() {
        return "hotswap-demo";
    }
}
