package com.example.javarestapp.controllers;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api")
public class ApiController {
    @Value("${javarestapp.url}")
    private String url;
    @Value("${javarestapp.path}")
    private String path;

    @RequestMapping(path="/{millis}", method=RequestMethod.GET)
    public Mono<String> get(@PathVariable Integer millis) {
        log.info("Thread # {}: Get ({}ms)", Thread.currentThread().getId(), millis);
        return process(millis);
    }

    @RequestMapping(method=RequestMethod.GET)
    public Mono<String> get() {
        log.info("Thread # {}: Get", Thread.currentThread().getId());
        return process(0);
    }

    private Mono<String> process(Integer millis) {
        log.info("Thread # {}: process ({}ms)", Thread.currentThread().getId(), millis);
        return WebClient
            .create(url)
            .get()
            .uri(String.format("%s/%d", path, millis))
            .retrieve()
            .bodyToMono(String.class)
            .doOnNext(x -> {
                log.info("Thread # {}: request succeeded -> {}", Thread.currentThread().getId(), x);
            })
            .delayElement(Duration.ofMillis(millis));
    }
}
