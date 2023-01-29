package com.example.commander.sender.command;

import com.example.commander.CommandSerializer;
import com.example.commander.domain.AsyncResult;
import com.example.commander.receiver.CommandController;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallbackController {

    private static final Logger logger = LoggerFactory.getLogger(CallbackController.class);
    private final CallableAsyncCommandManager callableAsyncCommandManager;
    private final CommandSerializer commandSerializer;

    public CallbackController(CallableAsyncCommandManager callableAsyncCommandManager,
                              CommandSerializer commandSerializer) {
        this.callableAsyncCommandManager = callableAsyncCommandManager;
        this.commandSerializer = commandSerializer;
    }

    @PostMapping("/asyncCallback")
    ResponseEntity<Void> callback(@RequestBody byte[] asyncResult){

        logger.debug("callback receive");
        var result = commandSerializer.deserialize(asyncResult, new TypeReference<AsyncResult>() {});

        callableAsyncCommandManager.callback(result);
        return ResponseEntity.ok().build();
    }


}
