from sage.all import *
from cryptography.hazmat.primitives import hashes
from os import urandom
from binascii import hexlify

# Define the type of polynomial
#PR.<X> = PolynomialRing(F)
# define the polynomial
#p = M[1]*X ** 2 + M[0] * X + key[0]
def p(k, m):
	res = key[0]

	i = len(m) - 1
	while (i >= 0):
		res = res + (m[i]*(k[1]**i))
		i -= 1
		
	return res

# F(valor) coloca o inteiro dentro do tipo que representa o tipo mod 2**130-5
# F = FiniteField -> um valor entre 0 e 2^130 - 5)
F = FiniteField(2**130-5) 

# define K1 and K2 has values of F
K1 = int.from_bytes(urandom(16), "big")
K2 = int.from_bytes(urandom(16), "big")

key = (F(K1),F(K2))

# define M as a list in F
# convert the 16 byte blocks into integers
M = []
f = open("m.txt", "rb")
while True:
	chunk = f.read(16)
	if chunk:
		integer_value = int.from_bytes(chunk, "big")
		M.append(F(integer_value))
	else:
		f.close()
		break

#M=[ F(34545435354), F(4329587293587958) ]

# compute the hash value
hash_value = p(key, M)

print("hash: " + str(hash_value))

