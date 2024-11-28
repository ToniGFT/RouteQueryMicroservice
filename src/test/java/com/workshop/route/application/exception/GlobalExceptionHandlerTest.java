package com.workshop.route.application.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Test Exception");
        ResponseEntity<String> response = globalExceptionHandler.handleGenericException(ex);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("An error occurred: Test Exception", response.getBody());
    }

    @Test
    void testHandleJsonMappingException() {
        JsonMappingException ex = new JsonMappingException(null, "Test JsonMappingException");
        ResponseEntity<String> response = globalExceptionHandler.handleJsonMappingException(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid JSON structure: Test JsonMappingException", response.getBody());
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Test RuntimeException");
        ResponseEntity<String> response = globalExceptionHandler.handleRuntimeException(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("An error occurred: Test RuntimeException", response.getBody());
    }
}
