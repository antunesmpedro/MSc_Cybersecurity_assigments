import sys
import json
import requests
import time
import os

clear = lambda: os.system('clear')

def register_pre_user():

	print('------------------------------')
	print(' REGISTER PRE USER')
	print()
	print('Your name: ')
	name = input()
	print()
	print('Your age: ')
	age = input()
	try:
		age = int(age)
	except:
		print('Age must be number!')
		time.sleep(1)
		clear()
		return "error"
	print()
	print('Your cellphone: ')
	cellphone = input()
	try:
		cellphone = int(cellphone)
	except:
		print('Cellphone must be number!')
		time.sleep(1)
		clear()
		return "error"
	print()
	print('Your cc bi: ')
	cc_bi = input()

	data_pre_user = {
		'name': name,
		'age': age,
		'cellphone': cellphone,
		'cc_bi': cc_bi
	}
	

	pre_user = json.loads(json.dumps(data_pre_user)) 
	print(pre_user)
	try:
		res = requests.post("https://127.0.0.1:5000/pre-register", json=pre_user, verify=False).json()
		# with web server production
		#res = requests.post("https://127.0.0.1-.5000/pre-register", json=pre_user, cert=('./client1-public-key.pem', './client1-private-key.pem'), verify='/home/pedroantunes/Desktop/seg_software/api/teste/ca_keys/ca-public-key.pem').json()
	
		#
		# TODO DOWNLOAD CLIENT CERTS AND client.py
		# 
		print()
		print(res)
	except:
		print("Maybe server is down!!")

	return

if __name__ == '__main__':

	resp = "error"
	while resp == "error":
		resp = register_pre_user()
