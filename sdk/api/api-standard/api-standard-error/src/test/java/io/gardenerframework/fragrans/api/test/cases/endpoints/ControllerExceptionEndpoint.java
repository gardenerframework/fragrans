package io.gardenerframework.fragrans.api.test.cases.endpoints;

import io.gardenerframework.fragrans.api.standard.error.exception.server.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 测试一下reactive的编程方式
 */
@RequestMapping("/controller/exception")
@RestController
public class ControllerExceptionEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionEndpoint.class);

    @RequestMapping
    public void exception(@RequestParam String exception, @RequestParam(required = false) Integer status) {
        try {
            if (status == null)
                throw (RuntimeException) Class.forName(exception).newInstance();
            else {
                throw new ResponseStatusException(HttpStatus.valueOf(status), HttpStatus.valueOf(status).getReasonPhrase(), new RuntimeException());
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            logger.error("cannot create exception instance", e);
            throw new InternalServerErrorException();
        }
    }

    @RequestMapping("/sendError")
    public void sendError(@RequestParam Integer status, HttpServletResponse response) throws IOException {
        response.sendError(status, HttpStatus.valueOf(status).getReasonPhrase());
    }
}