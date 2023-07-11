package com.amazonaws.samples.liver_disease;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient;
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClientBuilder;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointRequest;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;


public class LambdaFunctionHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private static String FEATURES = "features";
    private static String INSTANCES = "instances";
    private static String SAGEMAKER_ENDPOINT = System.getenv("SAGEMAKER_ENDPOINT");
    private static int LIVER_DISEASE_PREDICTION = 1;
    private int prediction_label = 0;
    private double score = 0.0;


    // Instantiate the clients
    SageMakerRuntimeClientBuilder builder = SageMakerRuntimeClient.builder();
    SageMakerRuntimeClient sageMakerRuntime = builder.build();
    public LambdaFunctionHandler() {
    }
    @Override
    public ApiGatewayResponse handleRequest(APIGatewayProxyRequestEvent  event, Context context) {
        return null;
    }
}
