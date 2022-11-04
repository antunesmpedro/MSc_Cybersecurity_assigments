# https://cryptography.io/en/3.0/hazmat/primitives/mac/hmac/#
from pwn import *
from binascii import hexlify, unhexlify
from cryptography.hazmat.primitives import hashes, hmac, serialization
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
import os
from cryptography.hazmat.primitives.asymmetric import rsa, padding

# https://cryptography.io/en/latest/hazmat/primitives/asymmetric/rsa.html

KEYLEN = 16

def H(x):
	digest = hashes.Hash(hashes.SHA256())
	digest.update(x.encode())
	return digest.finalize()

port = input()
r = remote("0.0.0.0",port)

# <->
key = hexlify(os.urandom(KEYLEN))

with open("pub.pem", "rb") as key_file:
	pk = serialization.load_pem_public_key(
		key_file.read()
	)
   
key_enc_by_pk = (pk.encrypt(key,padding.OAEP(mgf=padding.MGF1(algorithm=hashes.SHA256()),algorithm=hashes.SHA256(),label=None)))

r.sendline(hexlify(key_enc_by_pk))
key = key.decode()
# <->

nonce = H("IV"+key)
ke = H("KE"+key)
ka = H("KA"+key)
seq = 1

cipher = Cipher(algorithms.AES(ke), modes.CTR(nonce[16:]))
encryptor = cipher.encryptor()
f = open('slides.pdf', "rb")
while True:
	block = f.read(16)
	if block:
		h = hmac.HMAC(ka, hashes.SHA256(), backend=default_backend())
		seqb = seq.to_bytes(4,"little")
		cph = encryptor.update(block)
		h.update(cph+seqb)
		mac = h.finalize()
		r.sendline(hexlify(cph+mac))
		seq += 1
	else:
		f.close()
		break

r.sendline(hexlify(b'\xff'))
r.close()
