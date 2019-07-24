import requests
import json


item = {"firstName":"Ruth-Ann"}
params = {'where':json.dumps(item)}

headers = {
"X-Parse-Application-Id":"skillshop",
"X-Parse-REST-API-Key":"skillshop"
}

r = requests.get("http://skillshop2019.herokuapp.com/parse/classes/_User",headers=headers, params = params)


dic = json.loads(r.text)

token = dic["results"][0]["firebaseToken"]
data = {
 "to" : token,
 "notification" : {
     "body" : "your class has changed"
 }
}

params = {
  "apps": [
    {
      "serverURL": "http://skillshop2019.herokuapp.com/parse",
      "appId": "skillshop",
      "masterKey": "skillshop",
      "appName": "skillshop"
    }
  ]
}

headers = {'Content-type': 'application/json',"Authorization": "key=AAAAFMmfBZY:APA91bEKe0sf4q2m1z9htBYYGkZ8HR6HhAceKn9UBpW8E-q8bjbV9zxEd-750vq7Q97mDlKTWcqssy74xU86xnKB5jmh_X7q88aCN8B25Z-PMtyLI_QfnxKPVkI_cAxRtYmCTfUmr17z"}
r = requests.post('https://fcm.googleapis.com/fcm/send', data = json.dumps(data), headers=headers)


response = json.loads(r.text)
if(response["success"]):
    print("success")
else:
    print("fail")
