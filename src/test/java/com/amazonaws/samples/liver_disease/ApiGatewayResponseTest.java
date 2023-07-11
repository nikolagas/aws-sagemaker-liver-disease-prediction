package com.amazonaws.samples.liver_disease;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ApiGatewayResponseTest {

    @Test
    public void testApiGatewayResponse() {
        // Set up test data
        int statusCode = 200;
        String body = "Test body";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        boolean isBase64Encoded = false;

        // Create an instance of ApiGatewayResponse
        ApiGatewayResponse response = new ApiGatewayResponse(statusCode, body, headers, isBase64Encoded);

        // Assert the expected values
        Assertions.assertEquals(statusCode, response.getStatusCode());
        Assertions.assertEquals(body, response.getBody());
        Assertions.assertEquals(headers, response.getHeaders());
        Assertions.assertEquals(isBase64Encoded, response.isIsBase64Encoded());
    }
}