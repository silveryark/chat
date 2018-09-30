package com.silveryark.chat.controller;

import com.silveryark.rpc.Brokers;
import com.silveryark.rpc.GenericRequest;
import com.silveryark.rpc.GenericResponse;
import com.silveryark.rpc.gateway.OutboundMessage;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class BroadcastController {

    @Value("${broker}")
    private String broker;

    private final Brokers brokers;

    @Autowired
    public BroadcastController(Brokers brokers) {
        this.brokers = brokers;
    }

    @PostMapping("/messages")
    @PreAuthorize("hasRole('USER')")
    public Mono<GenericResponse> broadcast(Authentication user, @RequestBody GenericRequest request){
        OutboundMessage<Map<String, Object>> outboundMessage = new OutboundMessage<>("chat", null,
                new JSONObject()
                    .put("user", user.getPrincipal())
                    .put("content", request.getPayload())
                        .toMap());
        return brokers.create(broker)
                .body(outboundMessage)
                .retrieve(request.getRequestId());
    }
}
