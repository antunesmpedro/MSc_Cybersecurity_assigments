# https://cryptography.io/en/3.0/hazmat/primitives/mac/hmac/#
from pwn import *
from binascii import hexlify, unhexlify
from cryptography.hazmat.primitives import hashes, hmac
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes

def H(x):
	digest = hashes.Hash(hashes.SHA256())
	digest.update(x.encode())
	return digest.finalize()

#key = hexlify(b'g\xd7W\x00\xb3\x1c\xe8\xe3\x99\xf5\x18?\xe8\x14\xe2\xab').decode()
port = input()
key = "56ebc0ec8ed2f8829a5d3a0779fefece" # atualizar a cada itereção
nonce = H("IV"+key)
ke = H("KE"+key)
ka = H("KA"+key)
seq = 1
r = remote("0.0.0.0",port)

cipher = Cipher(algorithms.AES(ke), modes.CTR(nonce[16:]))
encryptor = cipher.encryptor()
f = open('slides.pdf', "rb")
while True:
	block = f.read(16)
	if block:
		h = hmac.HMAC(ka, hashes.SHA256(), backend=default_backend())
		#ciphertext, tag = cipher.encrypt_and_digest(block) -> can use dcm because 			different key to encrypt and to make the tag
		seqb = seq.to_bytes(4,"little")
		cph = encryptor.update(block)
		h.update(cph+seqb)
		mac = h.finalize()
		#HMAC_ver(ka, (cph+seqb), mac)
		r.sendline(hexlify(cph+mac))
		seq += 1
	else:
		f.close()
		break

r.sendline(hexlify(b'\xff'))
r.close()
