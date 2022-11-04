# Python Module ciphersuite
import os
import random
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes

# Security parameter (fixed)
KEYLEN = 16

# A service for generating keys and 
class Hsm:
	# (1) dictionary to store the pairs (hdl, key)
	def __init__(self):
		self.keys_handler = {}

	def gen(self): 
		# (1) create handler based on dictionary length and associate one random key
		handler = len(self.keys_handler) + 1
		self.keys_handler[handler] = os.urandom(KEYLEN)
		return handler

	def enc(self, handler, m): # (2) swap k to handler
		# (3) retrieve k with handler from dictionary
		k = self.keys_handler[handler]
		nonce = os.urandom(KEYLEN)
		cipher = Cipher(algorithms.AES(k), modes.CTR(nonce))
		encryptor = cipher.encryptor()
		cph = b""
		cph += encryptor.update(m)
		cph += encryptor.finalize()
		return (cph,nonce)

	def dec(self, handler, c, nonce): # (2) swap k to handler
		# (3) retrieve k with handler from dictionary
		k = self.keys_handler[handler]
		cipher = Cipher(algorithms.AES(k), modes.CTR(nonce))
		decryptor = cipher.decryptor()
		msg = b""
		msg += decryptor.update(c)
		msg += decryptor.finalize()
		return msg

# Trying out my hsm encryption service
# (4) Create HSM instance, generate key and get the handler. Use this handler instead of key
hsm = Hsm()
handler = hsm.gen()

# Encryption test
msg = b'Test message !!!'
(cph,nonce) = hsm.enc(handler, msg)
print("Here is my ciphertext:")
print(cph)
print('\n')

# Decryption test
msg_dec = hsm.dec(handler, cph, nonce)
print("Original message:")
print(msg)
print('\n')
print("Decrypted message:")
print(msg_dec)
print('\n')

# Final check
if (msg == msg_dec):
	print("All seems well!")
else:
	print("Error!")