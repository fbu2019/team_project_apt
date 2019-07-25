from flask import Flask, request
import send_notification
app = Flask(__name__)
@app.route("/")
def main():
    classId = request.args.get("classId")
    notificationsSent = send_notification.notifyTeacherStudents(classId)
    return "Notifications were sent to class {}".format(notificationsSent,classId)


if __name__ == "__main__":
    app.run(debug = True, host = "0.0.0.0", port = 80)
