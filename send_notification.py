import requests
import json
import pprint




def notifyTeacherStudents():
    params = {"include":"teacher"}

    headers = {
    "X-Parse-Application-Id":"skillshop",
    "X-Parse-REST-API-Key":"skillshop"
    }

    # r = requests.get("http://skillshop2019.herokuapp.com/parse/classes/_User",headers=headers, params = params)

    r = requests.get("http://skillshop2019.herokuapp.com/parse/classes/Workshop",headers=headers, params = params)

    response = json.loads(r.text)["results"]
    # pprint.pprint(response)
    for workshop in response:
        teacherObject = workshop["teacher"]
        teacherName = teacherObject["firstName"]+" "+teacherObject["lastName"]
        teacherObjectId = teacherObject["objectId"]

        students = workshop["students"]

        print('{} is teaching a class with {} students signed up'.format(teacherName, len(students)))
        print("These are the signed up students:")
        for studentId in students:
            params = {"where":json.dumps({"objectId":studentId})}
            studentRequest = requests.get("http://skillshop2019.herokuapp.com/parse/classes/_User",headers=headers, params = params)
            studentResponse = json.loads(studentRequest.text)["results"][0]

            sendNotification(studentResponse["firebaseToken"],"You are in a class taught by " + teacherName )
        sendNotification(teacherObject["firebaseToken"],"Your class has been edited")

def sendNotification(target,message):
    data = {
     "to" : target,
     "notification" : {
         "body" : message
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

if __name__ == "__main__":
    notifyTeacherStudents()
