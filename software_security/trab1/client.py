import os
import requests
import time
from getpass import getpass
import json


username = ""
user_id = ""
token = ""
headers = {'x-access-token': token}
clear = lambda: os.system('clear')

def register_user():

	print('------------------------------')
	print(' REGISTER USER')
	print()
	one_time_id = input('Your account id: ')
	print()
	username = input('Your username: ')
	print()
	password = getpass('Your password: ')
	print()

	data_user = {
		'username': username,
		'password': password,
	}
	

	user = json.loads(json.dumps(data_user)) 
	print(user)
	url = "https://127.0.0.1:5000/register/" + one_time_id
	try:
		res = requests.post(url, json=user, verify=False).json()
		print(res)
	except:
		print("Maybe server is down or Method not Allowed!!!!")


def login_user():
	print('------------------------------')
	print(' LOGIN USER')
	print()
	login_username = input('Your username: ')
	print()
	password = getpass('Your password: ')
	print()

	try:
		res = requests.get("https://127.0.0.1:5000/login", auth=(login_username,password), verify=False)
		print()
		
		time.sleep(1)
		if res.status_code == 200:
			print(":) Login accepted!")
			res_json = res.json()
			token = res_json['token']
			headers['x-access-token'] = token
			user_id = res_json['user_id']

			if login_username == "admin":
				print('|||||||||     SPECIAL ADMIN FEATURES    ||||||')
				print()
				print('           1- PROMOTE USERS')
				print('           2- DELETE USERS')
				print()
				print('           0- PROCEED TO MAIN MENU')
				while True:
					option = input('Insert you option: ')
					try:
						option = int(option)
						break
					except: 
						print(':( Numbers only!')
						time.sleep(1)
				if option == 1:
					try:
						res_get_all_users = requests.get("https://127.0.0.1:5000/users", headers=headers, verify=False).json()
						print()
						print('  USERS: ')
						print()
						print(res_get_all_users)
						print()
						promote_username = input('Insert username of the user that you want promote')
						url = "https://127.0.0.1:5000/user/" + promote_username
						res_promotion = requests.put(url, headers=headers, verify=False).json()
						print(res_promotion)
					except:
						print("Maybe server is down or Method not Allowed!!!!")
				if option == 2:
					try:
						res_get_all_users = requests.get("https://127.0.0.1:5000/users", headers=headers, verify=False).json()
						print()
						print('  USERS: ')
						print()
						print(res_get_all_users)
						print()
						delete_username = input('Insert username of the user that you want delete')
						url = "https://127.0.0.1:5000/user/" + delete_username
						res_deletion = requests.delete(url, headers=headers, verify=False).json()
						print(res_deletion)
					except:
						print("Maybe server is down or Method not Allowed!!!!")
						
			input('Press ENTER to go to the MAIN MENU')
			main_menu()
		else:
			print(str(res.status_code) + " " + res.content.decode("utf-8") + " " + res.headers['WWW-Authenticate'])
			print()
			input('Press ENTER to back the LOGIN MENU')
			login_user()
	except:
		print("Maybe server is down or Method not Allowed!!")
		

def chat_message():
	#
	# TODO: FINISH FUNCTION
	#

	clear()
	print('------------------------------')
	print(' CHAT MESSAGE')
	print()
	friend_username = input('   Insert username of the user you want: ')
	url = "https://127.0.0.1:5000/user/" + friend_username
	try:
		# this get is to get ip address of the username selected 
		# in our case the ip adress is same. only changes port
		res_get_friend_user = requests.get(url, headers=headers, verify=False).json()

		print()
		print('Do you want a connection to this user? (Y/N): ')
		conn = input()
		if conn == 'Y' or conn == 'y':
			print('Write your message: ')
			message = input()

			message_user = {
				'username': username,
				'message': message,
			}
			port = 5000 + int(res_get_friend_user['id'])
			url = "https://127.0.0.1:" + port
			res_send_message = requests.post(url, json=message_user, verify=False).json()
			print(res_send_message)
	except:
		print("Maybe server is down or Method not Allowed!!!!")

def main_menu():
	op = 6
	while op < 0 or op > 5:
		clear()
		print('------------------------------')
		print(' MAIN MENU')
		print()
		print('  1 - Get service #1 (square root)')
		print('  2 - Get service #2 (cube root)' )
		print('  3 - Get service #3 (n root)')
		print('  4 - Get all Users')
		print('  5 - (ChatMessage) Get one User')
		print()
		print('  0 - Back')
		print()
		op = input('  What you wanna do ? ')
		try:
			op = int(op)
		except:
			print(':( Numbers only!')
			time.sleep(1)
			op = 6

	if op == 0:

		login_menu()

	elif op >= 1 and op <=3:

		message_user = {}

		while True:
			number = input('Insert one number: ')
			try:
				number = float(number)
				break
			except:
				print(':( Floats only!')

		# create json object to post
		if op == 3:
			while True:
				root = input('Insert one root: ')
				try:
					root = float(root)
					break
				except:
					print(':( Floats only!')
			message_user = {
				'root': root,
				'number': number
			}

		else:
			message_user = {
				'number': number
			}


		try:
			url = "https://127.0.0.1:5000/service_" + str(op)
			res_service = requests.post(url, json=message_user, headers=headers, verify=False).json()
			print(res_service)
		except:
			print("Maybe server is down or Method not Allowed!!!!")

		print()
		input('Press ENTER to back the MAIN MENU')

	elif op == 4:

		try:
			res_get_all_users = requests.get("https://127.0.0.1:5000/users", headers=headers, verify=False).json()
			print(res_get_all_users)
			print()
			input('Press ENTER to back the MAIN MENU')
		except:
			print("Maybe server is down or Method not Allowed!!!!")
	else:
		#
		# TODO : p2p connection with another client
		#
		chat_message()

	main_menu()


def login_menu():

	op = 3
	while op < 0 or op > 2:
		clear()
		print('------------------------------')
		print(' CLIENT API')
		print()
		print('  1 - Register')
		print('  2 - Login')
		print()
		print('  0 - Exit')
		print()
		op = input('  What you wanna do ? ')
		try:
			op = int(op)
		except:
			print(':( Numbers only!')
			time.sleep(1)
			op = 3

	if op == 1:
		register_user()
		print()
		input('Press ENTER to go to the LOGIN MENU')

	login_user()	


if __name__ == '__main__':

	login_menu()

