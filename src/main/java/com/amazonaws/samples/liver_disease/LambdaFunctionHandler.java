package com.amazonaws.samples.liver_disease;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient;
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointRequest;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointResponse;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


public class LambdaFunctionHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private static String FEATURES = "features";
    private static String INSTANCES = "instances";
    private static String SAGEMAKER_ENDPOINT = System.getenv("SAGEMAKER_ENDPOINT");
    private static int LIVER_DISEASE_PREDICTION = 1;
    private int prediction_label = 0;
    private double score = 0.0;


    // Instantiate client
    SageMakerRuntimeClientBuilder builder = SageMakerRuntimeClient.builder();
    SageMakerRuntimeClient sageMakerRuntime = builder.build();

    public LambdaFunctionHandler() {
    }

    @Override
    public ApiGatewayResponse handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        if (event != null) {
            context.getLogger().log("Incoming event data " + event);
            JSONObject jsonObject = getEventData(event, context);

            // Build the list of features
            List<Object> featuresList = buildFeatures(jsonObject);

            context.getLogger().log("Features: " + featuresList);

            JSONObject request = buildRequest(featuresList);

            context.getLogger().log("SageMaker request data: " + request.toString());

            // Get inference response from SageMaker
            JSONObject response = getInference(request, context);

            if (response != null) {
                context.getLogger().log("Inference response data : " + response);
                JSONArray predictions = (JSONArray) response.get("predictions");
                Iterator<?> iter = predictions.iterator();
                while (iter.hasNext()) {
                    JSONObject prediction = (JSONObject) iter.next();

                    // Get the prediction label - either 0 or 1
                    prediction_label = (int) prediction.get("predicted_label");

                    // get the prediction score
                    score = (Double) prediction.get("score");
                    context.getLogger().log("Received prediction for liver disease with value of " + prediction_label);
                    context.getLogger().log("Prediction confidence level: " + score);
                }
            }
        }

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody("Prediction label is " + prediction_label + " with confidence of " + score)
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS API Gateway & Lambda Serverless"))
                .build();
    }


    private JSONObject buildRequest(List<Object> featuresList) {
        if (featuresList != null && !featuresList.isEmpty()) {
            JSONObject data = new JSONObject();
            JSONArray instances = new JSONArray();
            JSONObject features = new JSONObject();
            features.put(FEATURES, featuresList);
            instances.add(features);
            data.put(INSTANCES, instances);
            return data;
        }
        return null;
    }

    private JSONObject getEventData(APIGatewayProxyRequestEvent event, Context context) {
        context.getLogger().log("Event body is " + event.getBody());
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try {
            Object parsedObject = parser.parse(event.getBody());
            if (parsedObject instanceof JSONObject) {
                jsonObject = (JSONObject) parsedObject;
            } else {
                return null;
            }
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

    private JSONObject getInference(JSONObject request, Context context) {
        context.getLogger().log("Getting SageMaker inference to predict liver disease");

        InvokeEndpointRequest invokeEndpointRequest = InvokeEndpointRequest.builder()
                .contentType("application/json")
                .body(SdkBytes.fromByteBuffer(SdkBytes.fromUtf8String(request.toString()).asByteBuffer()))
                .endpointName(SAGEMAKER_ENDPOINT)
                .build();

        InvokeEndpointResponse result = sageMakerRuntime.invokeEndpoint(invokeEndpointRequest);

        String body = StandardCharsets.UTF_8.decode(result.body().asByteBuffer()).toString();

        JSONObject jsonResponse;
        try {
            jsonResponse = (JSONObject) new JSONParser().parse(body);
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }

        return jsonResponse;
    }

    private List<Object> buildFeatures(JSONObject jsonObject) {
        List<Object> features = new Vector<Object>();
        if (jsonObject != null) {
            features.add(jsonObject.get("age"));
            features.add(jsonObject.get("gender"));
            features.add(jsonObject.get("totalBilirubin"));
            features.add(jsonObject.get("directBilirubin"));
            features.add(jsonObject.get("alkalinePhosphotase"));
            features.add(jsonObject.get("alamineAminotransferase"));
            features.add(jsonObject.get("aspartateAminotransferase"));
            features.add(jsonObject.get("totalProteins"));
            features.add(jsonObject.get("albumin"));
            features.add(jsonObject.get("albuminGlobulinRatio"));
        }
        return features;
    }
}