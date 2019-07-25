import requests
import json
import pprint


def notifyTeacherStudents(target):

    # gets only the class with this object id and includes the teacher object in the response
    params = {"where":json.dumps({"objectId":target}),"include":"teacher"}

    # credentials we need to access the parse server
    headers = {
    "X-Parse-Application-Id":"skillshop",
    "X-Parse-REST-API-Key":"skillshop"
    }

    # parse get request for the workshop we want
    r = requests.get("http://skillshop-fbu.herokuapp.com/parse/classes/Workshop",headers=headers, params = params)

    #  from the response we save the workshop object
    workshop = json.loads(r.text)["results"][0]
    pprint.pprint(workshop)

    # take out the objects we need
    teacherObject = workshop["teacher"]
    teacherName = teacherObject["firstName"]+" "+teacherObject["lastName"]
    teacherObjectId = teacherObject["objectId"]
    students = workshop["students"]

    # for all the students this class has query the database for their tokens and send them a notification
    for studentId in students:
        params = {"where":json.dumps({"objectId":studentId})}
        studentRequest = requests.get("http://skillshop-fbu.herokuapp.com/parse/classes/_User",headers=headers, params = params)
        studentResponse = json.loads(studentRequest.text)["results"][0]

        sendNotification(studentResponse["firebaseToken"],"{} has edited the class \"{}\"".format(teacherName,workshop["name"]) )
    # send the teacher a notification to confirm that the class has been edited properly
    sendNotification(teacherObject["firebaseToken"],"Your class has been edited")


def sendNotification(target,message):
    # object to say who will recive the message and what it will say
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
    # credentials to access firebase
    headers = {'Content-type': 'application/json',"Authorization": "key=AAAAFMmfBZY:APA91bEKe0sf4q2m1z9htBYYGkZ8HR6HhAceKn9UBpW8E-q8bjbV9zxEd-750vq7Q97mDlKTWcqssy74xU86xnKB5jmh_X7q88aCN8B25Z-PMtyLI_QfnxKPVkI_cAxRtYmCTfUmr17z"}
    r = requests.post('https://fcm.googleapis.com/fcm/send', data = json.dumps(data), headers=headers)
