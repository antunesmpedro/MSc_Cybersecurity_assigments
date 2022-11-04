
def main():
	my_list = ['5e','5a','58','49','69','62','2b','77','7d','11','49','14','75','59','0','6c','44','a','4a','c','30','36','7b','14','7d','3f','33','66','34','6e','53','11','57','7','b','12']
	cont = 2
	flag = ""
	for x in my_list:
		local_c = cont * 3

		# Code to convert hex to binary (byte)
		local_38 = "{0:08b}".format(int(x, 16)) 
		print(local_38)
		
		# Code to convert decimal to binary (byte)
		local_c = bin(local_c).replace("0b", "").zfill(8)
		print(local_c)

		# Calculate xor of 2 strings
		xor_result = ""
		for i in range(8):
			if local_38[i] == local_c[i]:
				xor_result += "0"
			else:
				xor_result += "1"
		print(xor_result)

		# Code to convert binary(byte) to decimal
		dec_number = int(xor_result,2)
		print(dec_number)

		# Code to convert decimal to char
		char = chr(dec_number)
		print(char)
		
		flag += char

		print()
		cont += 1

	print(flag)

if __name__ == "__main__":
    main()
