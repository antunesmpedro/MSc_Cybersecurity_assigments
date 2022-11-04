from subprocess import PIPE, run
from binascii import hexlify
import hmac
import hashlib

cmd = ['openssl', 'dgst', '-hmac',  '1234', 'message.txt']
result = run(cmd, stdout=PIPE, universal_newlines=True)
parse_output = str(result.stdout).split(" ")
mac_openssl = parse_output[1]
mac_openssl = mac_openssl.rstrip("\n")
print("openssl: " + mac_openssl)


digest_maker = hmac.new(b'1234',  b'', hashlib.sha256)
f = open('message.txt', 'rb')
try:
    while True:
        block = f.read(16)
        if not block:
            break
        digest_maker.update(block)
finally:
    f.close()

digest = digest_maker.hexdigest()
print ("python hashlib: " + digest)

if(digest == mac_openssl):
	print("Sao iguais")
else:
	print("São diferentes")
