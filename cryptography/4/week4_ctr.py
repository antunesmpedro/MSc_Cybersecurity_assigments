# coding: utf-8
from os import urandom
from binascii import hexlify
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
key = urandom(16)
nonce = urandom(16)
cipher = Cipher(algorithms.AES(key), modes.CTR(nonce))
encryptor = cipher.encryptor()
file = open("aux.txt","r")
pt = file.read(16)
pt = pt.encode('UTF-8')
# What happens if you don't pass 16−byte input ? ANSWER: ERROR!!!
ct = encryptor.update(pt)
cphFile = open("ciphertext.bin","wb")
cphFile.write(ct)

#cipher = Cipher(algorithms.AES(key), modes.CBC(nonce+1))
#encryptor = cipher.encryptor()
pt = file.read(16)
pt = pt.encode('UTF-8')
ct = encryptor.update(pt) + encryptor.finalize()
cphFile.write(ct)


print(hexlify(key))
print(hexlify(nonce))

print("apagar")
chave = urandom(32)
nonce2 = urandom(8)
print(hexlify(chave))
print(hexlify(nonce2))
print("xau")