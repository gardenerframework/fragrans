package com.jdcloud.gardener.fragrans.api.test.endpoints;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.server.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * 测试一下reactive的编程方式
 */
@RequestMapping("/controller/exception")
@RestController
public class ReactiveExceptionEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(ReactiveExceptionEndpoint.class);

    @GetMapping
    public void exception(@RequestParam String exception, @RequestParam(required = false) Integer status) {
        try {
            if (status == null)
                throw (RuntimeException) Class.forName(exception).newInstance();
            else {
                throw new ResponseStatusException(HttpStatus.valueOf(status), HttpStatus.valueOf(status).getReasonPhrase(), new RuntimeException());
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new InternalServerErrorException();
        }
    }
}
