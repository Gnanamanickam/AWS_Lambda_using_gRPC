package Lambda;

import com.amazonaws.Response;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;

//import org.json.JSONObject;
//import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchTimeStamp implements RequestHandler<Map<String,String>, String> {

    boolean returnValue = false;

    @Override
    public String handleRequest(Map<String,String> event, Context context) {
        context.getLogger().log("Input value is " + event);
        LambdaLogger logger = context.getLogger();
        String response = new String("200 OK");
        AmazonS3 client = AmazonS3ClientBuilder.defaultClient();
        S3Object logFile = client.getObject("logfilegen", "input.txt");
        InputStream contents = logFile.getObjectContent();

        String output = new BufferedReader(
                new InputStreamReader(contents, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

//        try {
//            JSONObject logData = new JSONObject(output);
//        } catch (JSONException err) {
//            System.out.println("Exception : "+err.toString());
//        }

        String inputTime = event.get("inputTime");
        String differentialTime = event.get("differentialTime");
        long endTime;
        long startTime;

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        try {
            Date inputTimeFormat = format.parse(inputTime);
            Date inputDifferentialTimeFormat = format.parse(differentialTime);
            startTime = inputDifferentialTimeFormat.getTime() - inputTimeFormat.getTime();
            endTime = inputDifferentialTimeFormat.getTime() + inputTimeFormat.getTime();
            returnValue = checkValue(output, String.valueOf(startTime), String.valueOf(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.valueOf(returnValue);
    }

    public static boolean checkValue(String logData, String startTime, String endTime  ){

        String data[] = logData.split("/n");
        int startValue = 0;
        int endValue = data.length - 1;
        int midValue;
        boolean result = false;

        String timeStamp = "";

        while (startValue <=  endValue) {

            midValue = (startValue + endValue) / 2;
            timeStamp = data[midValue].split(" ")[0].split(".")[0];
            if (startTime.compareTo(timeStamp) > 0) {
                startValue = midValue + 1;
            } else if (endTime.compareTo(timeStamp) < 0) {
                endValue = midValue - 1;
            } else {
                result = true;
                break;
            }
        }

        return result;
    }

}
