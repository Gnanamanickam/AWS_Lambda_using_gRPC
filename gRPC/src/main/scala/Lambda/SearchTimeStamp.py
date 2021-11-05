import json
from datetime import datetime, timedelta
from time import strftime
import boto3

def lambda_handler(event, context):

    bucket = 'logfilegen'
    key = 'input.txt'
    s3 = boto3.client('s3')
    format = '%H:%M:%S'
    response = s3.get_object(Bucket=bucket, Key=key)
    data = response['Body'].read().decode('utf-8')

    inputTime = datetime.strptime(event['queryStringParameters']['inputTime'], format)
    inputTimeList = (event['queryStringParameters']['inputDifferentialTime']).split(":")

    startTime = (inputTime - timedelta(hours=int(inputTimeList[0])) - timedelta(minutes=int(inputTimeList[1])) - timedelta(seconds=int(inputTimeList[2]))).strftime(format)
    endTime = (inputTime + timedelta(hours=int(inputTimeList[0])) + timedelta(minutes=int(inputTimeList[1])) + timedelta(seconds=int(inputTimeList[2]))).strftime(format)

    logData = data.split('\n')
    startValue = 0
    endValue = len(logData) - 1
    returnValue = False
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

    response = {}
    response['isPresent'] = str(returnValue)

    # 2. Construct the body of the response object
    return {
        'statusCode': 200,
        'body': json.dumps(response)
    }