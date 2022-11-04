# coding: utf-8
from os import urandom
from binascii import hexlify
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
key = urandom(16)
iv = urandom(16)
cipher = Cipher(algorithms.AES(key), modes.CBC(iv))
encryptor = cipher.encryptor()
file = open("aux.txt","r")
pt = file.read(16)
pt = pt.encode('UTF-8')
# What happens if you don't pass 16−byte input ? ANSWER: ERROR!!!
ct = encryptor.update(pt)
cphFile = open("ciphertext.bin","wb")
cphFile.write(ct)

cipher = Cipher(algorithms.AES(key), modes.CBC(ct))
encryptor = cipher.encryptor()
pt = file.read(16)
pt = pt.encode('UTF-8')
ct = encryptor.update(pt) + encryptor.finalize()
cphFile.write(ct)


print(hexlify(key))
print(hexlify(iv))