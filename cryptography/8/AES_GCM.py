# https://github.com/jforissier/aesgcm -> decrypt
# ./aesgcm dec -key key.txt -iv iv.txt -in et.txt -out dt.txt -tag tag.txt 
# pip install pycrypto
#from Crypto.Cipher import AES
# pip install pycryptodomex
from Cryptodome.Cipher import AES
from binascii import hexlify, unhexlify
import os

def AES_GCM(key, data):

	nonce = os.urandom(12)

	print("\nNONCE:")
	print(hexlify(nonce))
	f = open("iv.txt", "wb")
	f.write(nonce)
	f.close()

	cipher = AES.new(key, AES.MODE_GCM, nonce=nonce)
	ciphertext, tag = cipher.encrypt_and_digest(data)
	
	f = open("et.txt", "wb")
	f.write(ciphertext)
	f.close()
	
	print("\nTAG:")
	print(hexlify(tag))
	
	print("Do you want to modify the ciphertext? [Y;N]")
	
	if(input() == "Y"):
		f = open("et.txt", "ab")
		f.write(os.urandom(2))
		f.close()
	# decrypt failed
	
	f = open("tag.txt", "wb")
	f.write(tag)
	#f.write(os.urandom(16)) -> Decrypt failed
	f.close()


if __name__ == "__main__":

	key = os.urandom(16)
	print("KEY: ")
	print(key)
	
	f = open("key.txt", "wb")
	f.write(key)
	f.close()
	
	print("Insert filename:")
	filename = input()

	with open(filename, "rb") as file:
		plaintext = file.read()
		file.close()
	
	AES_GCM(key, plaintext)
