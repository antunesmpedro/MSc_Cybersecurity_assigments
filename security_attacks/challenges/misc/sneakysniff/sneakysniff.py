def sxor(s1, s2):
    return "".join(chr(ord(a) ^ ord(b)) for a,b in zip(s1,s2))

def main():
	#print(sxor("22218800920401200184893195854511432461141272158758543013187113002803978", "Where is the flag? I dont know"))

    print(sxor("033824211e57580113270004720d3308346014166e550c5d2b745b01230a".decode("hex"), "Where is the flag? I dont know"))

if __name__ == "__main__":
    main()
