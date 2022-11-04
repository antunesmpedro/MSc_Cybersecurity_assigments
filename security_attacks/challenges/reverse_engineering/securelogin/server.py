#!/usr/bin/env python3

from secrets import flag
from secrets import admin_hash
import time, sys

def login():
	sys.stdout.write("Username: ")
	sys.stdout.flush()
	username = input()
	sys.stdout.write("Password hash: ")
	sys.stdout.flush()
	password_hash = input()
	if username != "admin":
		print("Invalid username")
		return

	print("Please wait...")
	sys.stdout.flush()

	#verify md5.hexdigest()[0:8]
	for i in range(8):
		if password_hash[i] != admin_hash[i]:
			print("Invalid password")
			return
		time.sleep(1)
	print("* Authenticated")
	print(flag)
	sys.stdout.flush()

if __name__ == "__main__":
	login()