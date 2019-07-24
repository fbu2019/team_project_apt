import requests
import json

data = {
 "to" : "e4Y9YhDvI9I:APA91bE7ExwQw6LYjRKiSb7bn90LTbvtgTWIq6b4mwuNuPLgQPKuQyNuytsBCR1FDBF6hJ-0ezp7EXsuAmGBt21jlhKo5Goeda2INN7UNzwmmGXb05fMSR0bydgaINXaNFxAgokKsNw1",
 "notification" : {
     "body" : "okok"
 }
}


headers = {'Content-type': 'application/json',"Authorization": "key=AAAAFMmfBZY:APA91bEKe0sf4q2m1z9htBYYGkZ8HR6HhAceKn9UBpW8E-q8bjbV9zxEd-750vq7Q97mDlKTWcqssy74xU86xnKB5jmh_X7q88aCN8B25Z-PMtyLI_QfnxKPVkI_cAxRtYmCTfUmr17z"}

r = requests.post('https://fcm.googleapis.com/fcm/send', data = json.dumps(data), headers=headers)

print(r.text)
