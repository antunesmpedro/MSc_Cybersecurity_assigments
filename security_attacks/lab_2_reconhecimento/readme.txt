|-----------------------------|
|                             |
|   LAB 02 - RECONHECIMENTO   |
|                             |
|           TARGET            |
|                             |
|         airbnb.com          |
|                             |
|-----------------------------|


1) Levantamento de email:

	qual os serviços de email utilizados pela empresa? 

		dig -t mx airbnb.com [+short]

		airbnb.com.		128	IN	MX	10 aspmx.l.google.com.
		airbnb.com.		128	IN	MX	20 alt1.aspmx.l.google.com.
		airbnb.com.		128	IN	MX	30 alt2.aspmx.l.google.com.
		airbnb.com.		128	IN	MX	40 aspmx2.googlemail.com.
		airbnb.com.		128	IN	MX	50 aspmx3.googlemail.com.

	Estão correctamente configurados (e.g. SPF)?

		dig -t txt airbnb.com [+short]

		airbnb.com.		299	IN	TXT	"50c24edbaddf46e8ad85f3b7aa6c19bb"
		airbnb.com.		299	IN	TXT	"facebook-domain-verification=t3zulila9jjnrfbyt6vjamhqdxz47f"
		airbnb.com.		299	IN	TXT	"MS=ms85621737"
		airbnb.com.		299	IN	TXT	"adobe-idp-site-verification=e68ae64c-3a65-4439-b368-0042e72c4cc8"
		airbnb.com.		299	IN	TXT	"google-site-verification=_e6Ayd8GN0S5dR116WkaM5ds5tx3ekRD5DC9-51pGrg"
		airbnb.com.		299	IN	TXT	"google-site-verification=HEoQR0xvby-BYv7pNt06jkvTF44l6rvMnREUZSTyfYQ"
		airbnb.com.		299	IN	TXT	"google-site-verification=A8e2zY9GYwx8D7x0aOz09Vl4gHfPmv86y_TbW1TUDqs"
		airbnb.com.		299	IN	TXT	"google-site-verification=Dz--VdnW2R_4K45T0eM8i0vNpFlip_o10WidHL5a3cg"
		airbnb.com.		299	IN	TXT	"webexdomainverification.=162d362c-c218-48f0-8b53-0017116e6f29"
		airbnb.com.		299	IN	TXT	"webexdomainverification.725890277b499457e053ab06fc0a5ef4=b060825b-4db4-4ce3-a61e-debc56370fc0"
		airbnb.com.		299	IN	TXT	"cisco-ci-domain-verification=3711d1eb98ba8919aa1dcfb052fe7e522dd1216a3a8ebf74aff61d7b37564e01"
		airbnb.com.		299	IN	TXT	"status-page-domain-verification=tb81t2ndk8pb"
		airbnb.com.		299	IN	TXT	"status-page-domain-verification=vxdvtv6jn2y9"
		airbnb.com.		299	IN	TXT	"docusign=1c606b88-26ef-48f9-8913-a4251a947532"
		airbnb.com.		299	IN	TXT	"docusign=d56dee1c-0384-4ffa-8801-e6bea308af97"
		airbnb.com.		299	IN	TXT	"atlassian-domain-verification=mvMbea0qZ4IoMr8/xjkRYgjiHikXBbF04PZnZYv1Nj9UgppCnagnNaSo4T8QnCnP"
		airbnb.com.		299	IN	TXT	"v=spf1 include:spf1.airbnb.com ip6:2c0f:fb50:4000::/36 ip6:2a00:1450:4000::/36 ip6:2800:3f0:4000::/36 ip6:2607:f8b0:4000::/36 ip6:2404:6800:4000::/36 ip6:2001:4860:4000::/36 ip4:76.223.176.0/20 ip4:74.85.25.0/27 ip4:74.125.0.0/16 ip4:72.14.192.0/18 -all"

		

	----------------------------------------------------------------------------------------------------------
		Types of rejection levels:
			-all (reject or fail them - don't deliver the email if anything does not match)
			~all (soft-fail them - accept them, but mark it as 'suspicious')
			+all (pass regardless of match - accept anything from the domain)
			?all (neutral - accept it, nothing can be said about the validity if there isn't an IP match)
--------------------------------------------------------------------------------------------------------------

2) Realizar a enumeração de subdomínios com o subfinder:

	subfinder -d airbnb.com > subdomains.txt

		[INF] Enumerating subdomains for airbnb.com
		[INF] Found 638 subdomains for airbnb.com in 30 seconds 23 milliseconds

--------------------------------------------------------------------------------------------------------------

3) Executar traceroute para um subdomínio à escolha, analisar a localização geográfica de cada IP da rota obtida (e.g. utilizar o serviço https://www.ip-tracker.org/)

	sudo traceroute -I pt.airbnb.com

		traceroute to pt.airbnb.com (104.126.108.214), 30 hops max, 60 byte packets
		 1  _gateway (10.0.2.2)  1.261 ms  1.171 ms  1.094 ms
		 2  192.168.1.254 (192.168.1.254)  4.252 ms  5.056 ms  4.989 ms
		 3  * * *
		 4  telepac15-hsi.cprm.net (195.8.30.246)  8.632 ms  9.645 ms  9.534 ms
		 5  bt-cr1-bu10-200.cprm.net (195.8.30.245)  13.266 ms  14.194 ms  14.145 ms
		 6  hu0-6-0-6.rcr21.opo01.atlas.cogentco.com (149.6.32.61)  10.907 ms  10.761 ms  11.602 ms
		 7  be2313.ccr31.bio02.atlas.cogentco.com (154.54.61.101)  20.531 ms  19.377 ms  21.185 ms
		 8  be2324.ccr31.mad05.atlas.cogentco.com (154.54.61.130)  25.787 ms  25.752 ms  25.696 ms
		 9  ft.mad05.atlas.cogentco.com (130.117.14.202)  27.157 ms  27.112 ms  27.047 ms
		10  * * *
		11  * * *
		12  a104-126-108-214.deploy.static.akamaitechnologies.com (104.126.108.214)  25.828 ms  26.689 ms  24.220 ms

	Localização geográfica:
		192.168.1.254   | IP Local (rede em Barcelos)
		195.8.30.246    | Lisboa, Portugal
		195.8.30.245    | Lisboa, Portugal
		149.6.32.61     | Madrid, Espanha
		154.54.61.101   | Colorado, Estados Unidos da América
		154.54.61.130   | Colorado, Estados Unidos da América
		130.117.14.202  | Paris, França
		104.126.108.214 | Colorado, Estados Unidos da América

--------------------------------------------------------------------------------------------------------------

4) Verificar quais os subdomínios que têm serviços http ou https activos com o httprobe. Armazenar o resultado num ficheiro, e.g. alive.txt

	cat subdomains.txt | go/bin/httprobe  > alive.txt

--------------------------------------------------------------------------------------------------------------

5) Levantamento de URLs: correr o dirsearch, ffuf ou gobuster contra pelo menos um host da superfície de ataque já identificada alive.txt

	python3 dirsearch.py -u http://email.airbnb.com -E
		Nada encontrado

	# Created a symbolic link to the tool using the ln -s command:
	# /bin# ln -s ~/dirsearch/dirsearch.py dirsearch
        
        dirsearch -u https://careers.airbnb.com -E
		Relatório preenchido (report_dirseach2.txt). mas com vários erros 4** encontrados
        dirsearch -u https://careers.airbnb.com -x 402,429 -E
                Relatório preenchido (report_dirseach3.txt)
        dirsearch -u https://careers.airbnb.com -x 402,429 -E -r
                Relatório preenchido (report_dirseach4.txt) com o a pesquisa recursiva de diretórios ativada
		
		Temos acesso a:
                https://careers.airbnb.com:443/favicon.ico
                https://careers.airbnb.com/robots.txt
	
--------------------------------------------------------------------------------------------------------------

EXTRA)  Correr Google Dorks para tentar encontrar ficheiros sensíveis pertencentes ao target

	Informações que serão indexadas pelo Google e outros motores de busca:
	(exceto as informações dentro dos diretorios/subdiretorios assinalados como "Disallow")
	
	### aceder a robots.txt
	filetype:txt inurl:airbnb.com  
	https://www.airbnb.pt/robots.txt
	
	### encontrar ficheiros com passwords e utilizadores
	site:airbnb.com "spring.datasource.password="   "spring.datasource.username=" ext:properties -git -gitlab 
	site:airbnb.com intext:admin.password
	site:airbnb.com "db.username"   "db.password" ext:properties
	site:airbnb.com allintext:username filetype:log

	### dorks para encontrar CVE-2020–25213 e outras vulnerabilidades(relatórios de segurança, chaves privades, certificados)
	site:airbnb.com inurl:/wp-content/plugins/wp-file-manager/readme.txt
	site:airbnb.com	intitle:"Vulnerability" ext:pdf 
	site:airbnb.com  inurl:8080   intext:"httpfileserver 2.3" 	
	site:airbnb.com inurl:node_modules/mqtt/test/helpers/ 
	site:airbnb.com intitle: "Index of" inurl:admin/uploads	
	site:airbnb.com intitle:"index of" "server.crt" | "server.csr"
	
	Nenhuma destas pesquisas teve sucesso !!!

	# https://www.techtudo.com.br/dicas-e-tutoriais/noticia/2011/07/guia-conheca-todos-os-comandos-de-buscas-do-google.html
	# https://www.youtube.com/watch?v=u_gOnwWEXiA
	
