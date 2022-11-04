from pwn import *
from binascii import hexlify, unhexlify
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
import os
import random

# bigx - from alice
# bigc - to alice
# bigy - from bob
# bigd - to bob

f = open("config.cfg", "w")
f.write("9951")
f.close()

# Fixed params for DH
p = 38747
g = 2

# TALK TO ALICE
l = remote("0.0.0.0", 9950)

# Generate c, C
sysrand = random.SystemRandom()
c = sysrand.randint(0,p)
bigc = (g**c) % p

# Send C, receive X
bigx = int.from_bytes(unhexlify(l.recvline(keepends=False)),"big")
l.sendline(hexlify(bigc.to_bytes(4,"big")))

# Get g^xy
secret_alice = (bigx**c) % p

# Generate key
digest = hashes.Hash(hashes.SHA256())
digest.update(secret_alice.to_bytes(4,"big"))
key_alice = digest.finalize()

# Receive secret message from Alice
cph_alice = unhexlify(l.recvline(keepends=False))

# GOAL
cipher_alice = Cipher(algorithms.AES(key_alice), modes.ECB())
decryptor = cipher_alice.decryptor()
msg = decryptor.update(cph_alice)

print("[TRUDY] Secret code:", hexlify(msg).decode())

l.close()

# TALK TO BOB
#r = remote("0.0.0.0", 9951)
r = listen(9951)
r.wait_for_connection()

# Generate d, D
sysrand = random.SystemRandom()
d = sysrand.randint(0,p)
bigd = (g**d) % p

# Send D, receive Y
r.sendline(hexlify(bigd.to_bytes(4,"big")))
bigy = int.from_bytes(unhexlify(r.recvline(keepends=False)),"big")

# Get g^xy
secret_bob = (bigy**d) % p

# Generate key
digest = hashes.Hash(hashes.SHA256())
digest.update(secret_bob.to_bytes(4,"big"))
key_bob = digest.finalize()

# Send secret message to bob
cipher_bob = Cipher(algorithms.AES(key_bob), modes.ECB())
encryptor = cipher_bob.encryptor()
cph_bob = encryptor.update(msg)
r.sendline(hexlify(cph_bob))

r.close()

