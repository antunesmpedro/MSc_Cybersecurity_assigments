FICHEIROS:
  scan_*.txt          -> Outputs de scans nmap
  netcat_refs         -> Links de apoio para o comando nc
  scapy_refs          -> Links de apoio para o scapy
  dump.pcap           -> captura utilizada para a pergunta EXTRA1.
  scapy_portScan.py   -> programa para a exploração de portas de um alvo

PDFs:
  nmap-mindmap.pdf           -> Imagem intuitiva com as opções nmap
  netcat_cheat_sheet_v1.pdf  -> Utilidades e exemplos do comando nc



LAB 03:
  I. Explorar as opções do nmap (ping scan, list scan, agressive)
      Ver ficheiro nmap_opcoes_exemplos.txt

  II. Realizar um scan dos serviços existentes em tpas.alunos.dcc.fc.up.pt
      Ver exemplos no ficheiro nmap_opcoes_exemplos.txt
      Alguns outputs estão nos ficheiros scan_*.txt

  III. Como realizar banner grabbing com netcat a um serviço? E.g. qual a versão SSH que está a correr em ssh.alunos.dcc.fc.up.pt
	echo "" | nc –v –n –w1 [TargetIPaddr] [start_port]-[end_port]
	ex. echo "" | nc ssh.alunos.dcc.fc.up.pt 22
		SSH-2.0-OpenSSH_8.3 
    
  IV. Observar um scan que estamos a realizar com o Wireshark - quais as opções que geram menos tráfego?
  	Feita a observação no wireshark a alguns exemplos de scan através do nmap, para não gerar muito tráfego na rede temos que:
		- restringir o número de portas a explorar (port range com opção -p)
                - os scan baseados em encaminhamento TCP geram muito mais tráfego do que os scans em UDP
                - a opção -sP (Ping Scan) não gera muito tráfego, apenas faz um ping ao alvo

  V. Como obter uma página HTTP via netcat? e.g. nc tpas.alunos.dcc.fc.up.pt 80
	echo "GET /index.html HTTP/1.1\r\nUser-Agent: nc/0.0.1\r\nHost: 193.136.39.125\r\nAccept: */*\r\n\r\n" | nc 193.136.39.125 80
  
  EXTRA1. Utilizar o Scapy para analisar um dump de pacotes gerado pelo Wireshark (e.g. extrair os IPs origem e destino de todos os pacotes)
    scapy
      >>> set((p[IP].src, p[IP].dst) for p in PcapReader('dump.pcap') if IP in p)
          {('104.20.64.56', '192.168.3.3'),
 	   ('192.168.3.1', '192.168.3.3'),
 	   ('192.168.3.3', '104.20.64.56'),
 	   ('192.168.3.3', '192.168.3.1'),
 	   ('192.168.3.3', '212.113.185.29'),
 	   ('192.168.3.3', '216.58.211.238'),
 	   ('212.113.185.29', '192.168.3.3'),
 	   ('216.58.211.238', '192.168.3.3')}
  EXTRA2. Construir um programa com o Scapy que faça port scanning a um determinado endereço IP ou hostname.
     executar programa python scapy_portScan.py
     PS: default ip = 'tpas.alunos.dcc.fc.up.pt' && tested ports = { 21, 22, 23, 25, 53, 69, 80, 88, 109, 110,
	123, 137, 138, 139, 143, 156, 161, 389, 443, 445, 500, 546,
	547, 587, 660, 995, 993, 1720, 2086, 2087, 2082, 2083, 3306, 5666, 8080, 8443, 10000 }
     
     sudo python3 scapy_portScan.py

     OUTPUT:
       Possible Open or Filtered Ports:
       [137, 138, 139, 8080, 21, 22, 23, 161, 5666, 1720, 443, 445, 80, 3306, 123]

