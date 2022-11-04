from sage.all import *
caesar = AffineCryptosystem ( AlphabeticStrings ())
a, b = (1, 3)
EncObject = caesar . encoding (" AttackAtDawn ")
print (EncObject)
Ciphertext = caesar . enciphering (a, b, EncObject )
print (Ciphertext)
Plaintext = caesar . deciphering (a, b, Ciphertext )
print (Plaintext)

# sage -python <filename>