package com.example.commander.sender.command;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallbackController {

    @PostMapping("asyncCallback")
    ResponseEntity<Void> callback(){
        return ResponseEntity.ok().build();
    }

}
