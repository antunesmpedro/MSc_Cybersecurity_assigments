from os import urandom
from binascii import hexlify
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend

key = urandom(16)
iv = urandom(16)
cipher = Cipher(algorithms.AES(key), modes.ECB(), backend=default_backend())
encryptor= cipher.encryptor()

# what happens if you don't pass the 16-byte input?
ct = encryptor.update(b"attack at dawn!!") + encryptor.finalize()
print(hexlify(key))
cphFile = open("ciphertext.bin", "wb")
cphFile.write(ct)
