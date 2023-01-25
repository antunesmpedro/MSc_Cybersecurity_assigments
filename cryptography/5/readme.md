|--------------------------------------|
|                                      |
|  Consolidation questions (optional)  |
|                                      |
|--------------------------------------|

1 - What kinds of stream cipher designs are there? And which kind is prevalent today?

    Nas cifras sequenciais temos desenhos que já são considerados inseguros (cifras quebradas) e outros desenhos que
    (até agora) são considerados seguros.
    As cifras sequenciais RC4 (anteriormente utilizada no protocolo TLS e na segurança de redes WiFi no WEP) e a
    A5/1 (utilizada na 2ª geração de telemóveis GSM) são cifras sequenciais inseguras nos dias de hoje.
    As cifras sequenciais AES em Counter Mode, a Grain-128a (orientadas por Hardware) e a ChaCha20 (orientada por Software) 
    são cifras sequenciais consideradas seguras.
    O tipo que prevalece hoje em dia é o AES em Counter Mode com chaves de 128-bits, utilização de Nonces e contadores
    para a geração de sequências de chaves (KSG). Contudo, a cifra ChaCha20 ganhou popularidade e é a cifra recomendada
    atualmente para o protocolo TLS 1.3, que é o protocolo das ligações HTTPS.

2 - Why would you combine a linear and a non-linear FSR together in the design of a stream cipher?

    O FSR (Feedback Shift-Registers) pega num registo R de tamanho l, e o último bit é tomado como o próximo bit da
    sequência de chaves (KS), isto é, Ki+1 := R[l]. Depois todo o array sofre um shift para a esquerda. O índice
    que ficasse em aberto é preenchido com uma função de realimentação.
    Como o FSR é periódico, se o registo tiver tamanho K vamos ter no máximo (2^K)-1 estados.
    O linear FSR (LFSR) utiliza funções de alimentação linear enquanto que o não-linear FSR (NFSR) utiliza funções
    de alimentação não-lineares. 
    Nas funções lineares temos uma interpretação algébrica que nos permite representar f como um polinómio. Com isto,
    sabemos que se tivermos um registo de tamanho K muito grande, o período pode nunca se repetir. O que nos dá
    uma maximização de segurança destes registos. No entanto como f é linear, com apenas alguns bits da sequência
    de chaves podemos resolver o polinómio através de um sistema de equações e por aqui saber os bits todos da 
    sequência de chaves.
    Nas funções não-lineares removemos a vulnerabilidade associada à lineariedade. No entanto, com funções
    não-lineares é difícil ter a certeza de que temos períodos grandes e que temos segurança.

    Posto isto, devemos sempre combinar LFSR (para garantir grandes períodos) com NFSR (para ter a segurança da
    sequência de chaves).


3 - Explain how RC4 was used in TLS multiple times with the same key, despite not having an explicit nonce input.
    What is the problem with this approach?

    Para cada sessão HTTPS que utilizava o protocolo TLS com cifras RC4, era inicializada uma chave de sessão 
    de 128-bits como uma permutação pseudo-aleatória. Em cada nova página que faziamos download na mesma sessão,
    simplesmente tiravamos do gerador de chaves para nunca utilizarmos a mesma chave na cifração de texto limpo.
    Nunca faziamos reset ao gerador de chaves. Logo, para páginas muito longas iamos tirar muitos bytes do gerador.
    Com isto, obtemos desvios estatísticos que nos davam correlações que o RC4 utilizava para gerar muitos bytes.
    Isto tornou-se um problema nesta abordagem.

4 - Give an example of a modern stream cipher that is recommended for widespread use.

    Uma cifra sequencial recomendada para o uso generalizado é a AES em Counter Mode. No entanto, a cifra
    ChaCha20 é a recomendada para o protocolo TLS 1.3 por ser muito mais eficiente do que o AES quando o
    AES é implementada em software.

|------------------------------------------|
|                                          |
|  Guided practical assignment (optional)  |
|                                          |
|------------------------------------------|

2 - Obtain a Python implementation of RC4 from the web and use it to encrypt a file.

    Implementação da cifra RC4 para cifrar uma string.

     chave := 'not-so-random-key'  
     string := 'Good work! Your implementation is correct'

     python3 rc4-py3.py

     FONTE: https://github.com/manojpandey/rc4

3 - Check that this algorithm is compatible with OpenSSL

	chave := '4d97cebab9d7f5576838ec05769832c2'

    cifrar := openssl enc -rc4 -nopad -K '4d97cebab9d7f5576838ec05769832c2' -in aux.txt -out ciphertextRC4.enc
    
    decifrar := openssl enc -d -rc4 -nopad -K '4d97cebab9d7f5576838ec05769832c2' -in ciphertextRC4.enc -out auxRC4.new

4 - Demonstrate with OpenSSL that ChaCha20 produces a repeated ciphertext if you encrypt the same file with the
same key and nonce.

	chave := '3e5c5d892c611cdadb7745631cdef8864f5f7292c413b62acf55d6704e2d4fc2' (256-bits)
    nonce := '57178571a64898cb' (64-bits)
    ficheiro := (320-bits)

    # primeira iteração

    cifrar := openssl enc -chacha20 -nopad -K '3e5c5d892c611cdadb7745631cdef8864f5f7292c413b62acf55d6704e2d4fc2' -iv '57178571a64898cb' -in aux2.txt -out ciphertextChaCha20.enc
    
    decifrar := openssl enc -d -chacha20 -nopad -K '3e5c5d892c611cdadb7745631cdef8864f5f7292c413b62acf55d6704e2d4fc2' -iv '57178571a64898cb' -in ciphertextChaCha20.enc -out auxChaCha20.new

    # segunda iteração (ficheiros no formato *_2.???)

    cifrar := openssl enc -chacha20 -nopad -K '3e5c5d892c611cdadb7745631cdef8864f5f7292c413b62acf55d6704e2d4fc2' -iv '57178571a64898cb' -in aux2.txt -out ciphertextChaCha20_2.enc
    
    decifrar := openssl enc -d -chacha20 -nopad -K '3e5c5d892c611cdadb7745631cdef8864f5f7292c413b62acf55d6704e2d4fc2' -iv '57178571a64898cb' -in ciphertextChaCha20_2.enc -out auxChaCha20_2.new

    #comparação dos dois ficheiros

    diff ciphertextChaCha20.enc ciphertextChaCha20_2.enc
    diff auxChaCha20.new auxChaCha20_2.new

