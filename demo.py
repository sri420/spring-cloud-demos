import os
import boto3
import json
import requests
from requests_aws4auth import AWS4Auth

region = os.environ['region']  # 'us-east-2'
service = 'es'
credentials = boto3.Session().get_credentials()
awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

HOST=os.environ['ES_DOMAIN'] # 'https://vpc-my-es-domain-4tv7tiqluavqdnpmfkdknjcpjm.us-east-2.es.amazonaws.com'
BULK="_bulk"

headers = { "Content-Type": "application/json" }

body=''
queryStringParameters=''
pathParameters=''


def doESPost(url,queryStringParameters,headers,body):
    if BULK in url:
       body=body + "\n"   
    r = requests.post(url , params = queryStringParameters, auth=awsauth, headers=headers , data=body)
    return r.text
    
def doESPut(url,queryStringParameters,headers,body):
    r = requests.put(url , params = queryStringParameters, auth=awsauth, headers=headers , data=body)
    return r.text

def doESDelete(url,queryStringParameters,headers,body):
    r = requests.delete(url , params = queryStringParameters, auth=awsauth, headers=headers , data=body)
    return r.text    

def doESGet(url,queryStringParameters,headers,body):
    r = requests.get(url , params = queryStringParameters, auth=awsauth, headers=headers , data=body)
    return r.text    


def handler(event, context):

    resource=event['resource']
    path=event['path']
    httpMethod=event['httpMethod']

    queryStringParameters = event['queryStringParameters'] if event['queryStringParameters'] else ''
    print('queryStringParameters:' + str(queryStringParameters))

    if ( event['pathParameters'] ):
        pathParameters=event['pathParameters']
        if ( pathParameters ):
           print('pathParameters:' + str(pathParameters))
           pathVariables=pathParameters['indexName']
        else:
           pathVariables=''
    
    if httpMethod == 'POST' or httpMethod == 'PUT':
       body=event['body']
    else:
       body=''

    
    response = {
                "statusCode": 200,
                "headers": 
                 {
                    "Access-Control-Allow-Origin": '*'
                 },
                 "isBase64Encoded": False
         }
         
    url=HOST + '/' + pathVariables
    #print('url:' + url)
    
    
    if httpMethod == 'GET':
       response['body']=doESGet(url,queryStringParameters,event['headers'],body)
       return response
    elif httpMethod == 'POST':
         response['body']=doESPost(url,queryStringParameters,event['headers'],body)
         return response
    elif httpMethod == 'PUT':
         response['body']=doESPut(url,queryStringParameters,event['headers'],body)
         return response     
    elif httpMethod == 'DELETE':
         response['body']=doESDelete(url,queryStringParameters,event['headers'],body)
         return response    
         
         
