import json
import boto3
from datetime import datetime, timedelta
from time import strftime

s3 = boto3.client('s3')

def lambda_handler(event, context):

    bucket = 'logfilegen'
    key = 'input.txt'
    format = '%H:%M:%S'
    returnValue = false

    try:
        data = s3.get_object(Bucket=bucket, Key=key)
        json_data = data['Body'].read().decode('utf-8')

        inputTime = datetime.strptime(event["queryStringParameters"]['inputTime'], format )
        inputTimeList = (event["queryStringParameters"]['inputDifferentialTime']).split(":")

        startTime = (inputTime - timedelta(hours=int(inputTimeList[0])) - timedelta(minutes=int(inputTimeList[1])) - timedelta(seconds=int(inputTimeList[2]))).strftime(format)
        endTime = (inputTime + timedelta(hours=int(inputTimeList[0])) + timedelta(minutes=int(inputTimeList[1])) + timedelta(seconds=int(inputTimeList[2]))).strftime(format)

        logData = data.split('/n')
        startValue = 0
        endValue = len(logData) - 1

        while startValue <= endValue:
            midValue = (startValue + endValue) // 2
            timestampValue = logData[midValue].split(" ")[0].split(".")[0]
            if startTime > timestampValue:
                startValue = midValue + 1
            elif endTime < timestampValue:
                endValue = midValue - 1
            else:
                returnValue = True
                break

        # 2. Construct the body of the response object
        transactionResponse = {}
        transactionResponse['returnValue'] = str(returnValue)

        # Construct http resonse
        responseObject = {}
        responseObject['StatusCode'] = 200
        responseObject['headers'] = {}
        responseObject['headers']['Content-Type'] = 'application/json'
        responseObject['body'] = json.dumps(transactionResponse)

        # 4. Return the response object
        return responseObject

    except Exception as e:
        print(e)
        raise e