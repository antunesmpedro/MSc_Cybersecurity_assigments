from pwn import *
from binascii import hexlify, unhexlify

port = input()

r = remote("0.0.0.0",port)

print(r.recvline().decode())
print(r.recvline().decode())

# <->
r.sendline('0')
print(r.recvline().decode())
m0 = hexlify(b'Attack at dawn0!').decode()
r.sendline(m0)
c0 =  r.recvline().decode()

# <->
r.sendline('1')

print(r.recvline().decode())

r.sendline(hexlify(b'Attack at dawn0!').decode())
r.sendline(hexlify(b'Attack at dusk0!').decode())

c_received = r.recvline().decode()
print("Received ciphertext: " + c_received)
print("\n")

# <->
r.sendline('2')

print(r.recvline().decode())

print("Guess")

if c_received == c0: guess_value = 0
else: guess_value = 1

print(guess_value)

r.sendline(str(guess_value))

print(r.recvline().decode())

r.close()
