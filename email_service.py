from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

SENDGRID_API_KEY = "your-sendgrid-api-key"  # Replace with your API key
SENDGRID_URL = "https://api.sendgrid.com/v3/mail/send"
SENDER_EMAIL = "your-sender-email@example.com"  # Replace with your verified sender email

@app.route('/send-email', methods=['POST'])
def send_email():
    data = request.json
    recipient_email = data.get('recipient')
    subject = data.get('subject')
    message = data.get('message')

    if not recipient_email or not subject or not message:
        return jsonify({"error": "All fields are required"}), 400

    email_data = {
        "personalizations": [
            {
                "to": [{"email": recipient_email}],
                "subject": subject
            }
        ],
        "from": {"email": SENDER_EMAIL},
        "content": [{"type": "text/plain", "value": message}]
    }

    headers = {
        "Authorization": f"Bearer {SENDGRID_API_KEY}",
        "Content-Type": "application/json"
    }

    response = requests.post(SENDGRID_URL, json=email_data, headers=headers)

    if response.status_code == 202:
        return jsonify({"success": "Email sent successfully"}), 200
    else:
        return jsonify({"error": response.json()}), response.status_code

if __name__ == '__main__':
    app.run(debug=True)

