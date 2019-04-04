package com.ttn.reap.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLIntegrityConstraintViolationException;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEmail extends SQLIntegrityConstraintViolationException {
    public DuplicateEmail(String msg) {
        super(msg);
    }
}
