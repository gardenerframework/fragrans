package io.gardenerframework.fragrans.api.test.cases.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ResponseStatusAnnotatedException extends RuntimeException {
}
