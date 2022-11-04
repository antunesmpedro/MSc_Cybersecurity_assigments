from pwn import *
from binascii import hexlify, unhexlify

port = input()
r = remote("0.0.0.0",port)

r.recvline()
r.recvline()

#
r.sendline("0")
r.recvline()
M1 = b'abcdefghijklmnop'
r.sendline(hexlify(M1))
T1 = r.recvline()
print(len(T1))
T1 = T1.rstrip()
T1 = unhexlify(T1)

#
r.sendline("0")
r.recvline()
M2 = b'qrstuvwxyzabcdef'
r.sendline(hexlify(M2))
T2 = r.recvline()
T2 = T2.rstrip()
T2 = unhexlify(T2)

#
M3 = b''
M3 += M1
M3 += bytes(a ^ b for a,b in zip(M2,T1))

#
r.sendline("1")
r.recvline()
r.sendline(hexlify(M3))
r.sendline(hexlify(T2))
print(r.recvline())

#
r.sendline("2")
r.close()
