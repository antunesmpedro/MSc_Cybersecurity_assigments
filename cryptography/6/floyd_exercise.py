# sudo apt-get install build-essential libssl-dev libffi-dev python3-dev
# sudo pip3 install cryptography==3.1

from pwn import *
from cryptography.hazmat.primitives import hashes
import os
import time

L = 5 # output length in bytes

# Something to make calling hash functions more succint
def H(X):
	digest = hashes.Hash(hashes.SHA256())
	digest.update(X)
	return (digest.finalize()[0:L])

# Write a function that finds the collision and presents the values in which it occurred
def floyd(h0):
	print("Hash is "+str(8*L)+" bits")

	# Your code here!!
	# cycle detectin and find hi where hi = h_i
	start = time.time()
	hi = H(h0)
	h_i = H(H(h0))
	it = 0
	while hi != h_i:
		hi = H(hi)
		h_i = H(H(h_i))
		it += 1

	print("iterations of cycle detection: " + str(it))
	
	# minimum iterations to find the colision point (u) and find the preimages
	u = 0
	hi = h0
	while hi != h_i:
		preimage1 = hi
		preimage2 = h_i
		hi = H(hi)
		h_i = H(h_i)
		u += 1
	end = time.time()	
	
	print(end - start)
	
	print("u iterations to colision/cycle: " + str(u))
	
	print("preimage1: " + str(preimage1))
	print("Compare to h1 (should be equal): " + str(H(preimage1)))
	print("preimage2: " + str(preimage2))
	print("Compare to h1 (should be equal): " + str(H(preimage2)))

	return (h0, hi)

start = os.urandom(L)
(h0, h1) = floyd(start)
print("h0: " + str(h0))
print("h1: " + str(h1))


# Complexidade: 4 x 2^(4L) -> exponencial
# L = 1byte = 8 bits -> +- 0.03s
# L = 2byte -> +- 0.04s
# L = 3 -> 0.23s
# L = 4 -> 0.5s - 2.5s (ja começamos a ver maiores variaçoes de tempo devido a termos mais possiveis indexes u para a colisao)
# L = 5 -> +- 40s - 105s
