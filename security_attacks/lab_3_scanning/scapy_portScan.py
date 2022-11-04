from scapy.all import *


#  create and send an ICMP packet to the host. The host will respond if it is up.
def is_up(ip):
	icmp = IP(dst=ip)/ICMP()
	resp = sr1(icmp, timeout=10)
	if resp == None:
		return False
	else:
		return True



#  function to scan the port using SYN packets
#
#
#  Here we set a random port as the destination port, and then create a SYN packet with the source port,
#  destination port, and destination IP. Then we will send the packet and analyze the response. 
#  If the response type is None, then the port is closed. 
#  If the response has a TCP layer, then we have to check the flag value in it.
#  The flag has nine bits, but we check for the control bits, which have six bits. They are:
# 
#  • URG = 0x20
#  • ACK = 0x10
#  • PSH = 0x08
#  • RST = 0x04
#  • SYN = 0x02
#  • FIN = 0x01 
#
#  So, if the flag value is 0x12, then the response has a SYN flag and we can consider the port to be open.
#  If the value is 0x14, then the flag is RST/ACK, so the port is closed.
def probe_port(ip, port):
	src_port = RandShort()
	result = 1
	try:
		p = IP(dst=ip)/TCP(sport=src_port, dport=port, flags="S")
		resp = sr1(p, timeout=10) # Sending packet
		if str(type(resp)) == "<type 'NonType'>":
			print ("Closed port "+port)
			result = 0
		elif resp.haslayer(TCP):
			if resp.getlayer(TCP).flags == 0x14:
				print ('Closed port {}'.format(port))
				result = 0
			elif resp.getlayer(TCP).flags == 0x12:
				send_rst = sr(IP(dst=ip)/TCP(sport=src_port,dport=port,flags='AR'),timeout=10)
				print ('Open port {}'.format(port))
				result = 1
			elif (int(resp.getlayer(ICMP).type) == 3 and
				int(resp.getlayer(ICMP).code) in [1,2,3,9,10,13]):
				print ('Filtered port {}'.format(port))
				result = 2
	except Exception as e:
		pass
	return result


#  Each port from the common port list is scanned and the identified open ports are added to 
#  the open ports list, following the list that is printed
if __name__ == "__main__":

	#  vars
	host = 'tpas.alunos.dcc.fc.up.pt'
	ip = socket.gethostbyname(host)
	open_ports = []
	filterdp = []
	common_ports = { 21, 22, 23, 25, 53, 69, 80, 88, 109, 110,
	123, 137, 138, 139, 143, 156, 161, 389, 443, 445, 500, 546,
	547, 587, 660, 995, 993, 1720, 2086, 2087, 2082, 2083, 3306, 5666, 8080, 8443, 10000 }

	#common_ports = range(1,10000)


	if is_up(ip):
		for port in common_ports:
			print(port)
			response = probe_port(ip, port)
			if response == 1:
				open_ports.append(port)
			elif response == 2:
				filterdp.append(port)

		if len(open_ports) != 0:
			print ("Possible Open or Filtered Ports:")
			print (open_ports)
		if len(filterdp) != 0:
			print ("Possible Filtered Ports:")
			print (filterdp)
		if (len(open_ports) == 0 and len(filterdp) == 0):
			print ("Sorry, No open ports found!!!!")
	else:
		print ("Host is Down!")


