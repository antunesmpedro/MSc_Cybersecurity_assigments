# extracting zip with password
import zipfile


def main():
    zipfile_name = 'flag.zip'
    filename= 'rockyou.txt'

    #zipfile = zipfile.ZipFile(file_name)
    #pwds = open("ro")
    with zipfile.ZipFile(zipfile_name) as file:
        with open(filename, encoding='utf8') as pwds:
            password = pwds.readline()
            password = password.replace('\n','\0')
            #end = len(password)
            #password
            #print(password[0:end])
            cont = 1
            while password:
            	file.setpassword(bytes(password, encoding='utf8'))
            	print(len(password))
            	print(password)

            	try:
            		# file.extract('flag.txt'))
            		file.extractall('flag.txt', pwd = bytes(password, encoding='utf-8'))
            		print("password: ", password)
            		return ;
            	except Exception as e:
            		print(e)
            	password = pwds.readline()


if __name__ == "__main__":
    main()
