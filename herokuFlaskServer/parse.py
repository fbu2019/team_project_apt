from flask import Flask,request
import send_notification
app = Flask(__name__)
@app.route("/")
def parse():
    classId = request.args.get("classId")
    notificationsSent = send_notification.notifyTeacherStudents(classId)
    return "Notifications were sent!\n"
