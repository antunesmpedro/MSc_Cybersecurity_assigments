from sage.all import *


#S = [i for i in range(0,255+1)]
#P

#X = DiscreteProbabilitySpace(S,P)
#print (X.domain())
#print (X.set())
#print (X.entropy())



k = 10
S = randint(0,2^k)
S.entropy