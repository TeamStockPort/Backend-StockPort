package com.stockport.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cicd")
public class CicdController {

    @GetMapping("/test")
    public String cicdTest() {
        return "CICD pipeline is working!";
    }
}
