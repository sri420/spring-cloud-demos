
function getQueryParams(event){
  var querystring='';
  if( event.queryStringParameters && event.queryStringParameters.q ){
    querystring=event.queryStringParameters.q;
    console.log('querystring:' + querystring); 
  }
  return querystring;
}

function makeElasticSearchRequest(request,callback){
  var credentials = new AWS.EnvironmentCredentials('AWS');
  var signer = new AWS.Signers.V4(request, 'es');
  signer.addAuthorization(credentials, new Date());
 
  var client = new AWS.HttpClient();
 
  console.log('makeRequest:Request:',JSON.stringify(request, null, 2));
 
  client.handleRequest(request, null, function(response) {
    console.log(response.statusCode + ' ' + response.statusMessage);
    var responseBody='';
    response.on('data', function (chunk) {
      responseBody += chunk;
    });
    response.on('end', function (chunk) {
      console.log('Response body: ' + responseBody);
      callback(undefined,responseBody);
    });
  }, function(error) {
    console.log('Error: ' + error);
    callback(error,undefined);
  });
  
}

function createElasticSearchRequest(event){
    console.log('Entering createESRequest.');
    
    console.log('method:' + event.httpMethod);
    console.log('path:' + event.path);
    console.log('body:' + event.body);
    
    var request = new AWS.HttpRequest(endpoint, region);

    request.method = event.httpMethod;
    
    var queryparam=getQueryParams(event);
    
    console.log('queryparam:',queryparam);
    
    if(queryparam) {
      request.path = event.path + '?q=' + queryparam;
    }
    else{
      request.path=event.path;
    }
    
    if ( event.path.includes(BULK)) {
      request.body = event.body + NEW_LINE;
    }
    else{
      request.body = event.body;
    }
    
    request.headers['host'] = domain;
    request.headers['Content-Type'] = 'application/json';

    return request;

}
function performESRequest(event,callback){

console.log('Entering performESRequest.');

var request=createElasticSearchRequest(event);

makeElasticSearchRequest( request,  (error, data)=> {
    console.log('performESRequest: error ',error);
    console.log('performESRequest: data ',data);
    callback(error,data);
});

}


var AWS = require('aws-sdk');
//To Get From Environmental Variable
const domain = process.env.ES_DOMAIN; 
//'vpc-my-es-domain-4tv7tiqluavqdnpmfkdknjcpjm.us-east-2.es.amazonaws.com'; 

//To Get From Environmental Variable
const region = process.env.REGION
//'us-east-2'; 

const BULK="/bulk";
const NEW_LINE='\n';

var endpoint = new AWS.Endpoint(domain);

var apiresponse = {
        "statusCode": 200,
        "body": "",
        "isBase64Encoded": false
      }; 

exports.handler =  (event,context, callback) => {

    //Log the Received Event
    console.log(event);
    
    //Make the ES Request and Get Back Response
    performESRequest( event,  (error, data)=> {
            apiresponse.body=data;
            callback(error,apiresponse);
    });

};
