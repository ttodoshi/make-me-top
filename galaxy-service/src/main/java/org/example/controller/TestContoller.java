package org.example.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestContoller {

    @PostMapping("/user")
    public String test(@RequestBody testModel testModel) {
        return "{\"openSystemList\":[4,6,7,8,9,10,11],\"closeSystemList\":[12,13,14,15,16,17,18,19,20,21,22,23,24],\"educationSystemList\":[{\"systemId\":1,\"completed\":25},{\"systemId\":2,\"completed\":50},{\"systemId\":3,\"completed\":75},{\"systemId\":6,\"completed\":100}]}";
    }
}
