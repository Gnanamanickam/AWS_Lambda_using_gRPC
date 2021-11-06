import json
from datetime import datetime, timedelta
from time import strftime
import hashlib
import boto3

def lambda_handler(event, context):

    bucket = 'logfilegen'
    key = 'input.txt'
    s3 = boto3.client('s3')
    format = '%H:%M:%S'
    response = s3.get_object(Bucket=bucket, Key=key)
    data = response['Body'].read().decode('utf-8')
    logData = data.split('\n')
    startValue = 0
    endValue = len(logData) - 1
    returnValue = False

    inputTime = datetime.strptime(event['queryStringParameters']['inputTime'], format)
    TimeSplit = (event['queryStringParameters']['inputDifferentialTime']).split(":")

    startTime = (inputTime - timedelta(hours=int(TimeSplit[0])) - timedelta(minutes=int(TimeSplit[1])) - timedelta(seconds=int(TimeSplit[2]))).strftime(format)
    endTime = (inputTime + timedelta(hours=int(TimeSplit[0])) + timedelta(minutes=int(TimeSplit[1])) + timedelta(seconds=int(TimeSplit[2]))).strftime(format)

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
    response['isTimeStampPresent'] = str(returnValue)
    response['hashValue'] = str(hashlib.md5(str(returnValue).encode()))


    return {
        'statusCode': 200,
        'body': json.dumps(response)
    }