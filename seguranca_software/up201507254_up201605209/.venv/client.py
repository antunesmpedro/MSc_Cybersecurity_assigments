import sys
import json
import requests

conv = {'name': 'teste', 'password': 'Greeting'}
s = json.dumps(conv) 
print(s)
res = requests.post("http://127.0.0.1:5000/user", json=s).json()
print(res)
