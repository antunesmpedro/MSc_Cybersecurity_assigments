|-------------------------|
|                         |
| Consolidation questions |
|                         |
|-------------------------|

1 - What is a block cipher and what is a secure block cipher?

    Uma cifra por blocos são dois algoritmos determinísticos (não usa aleatoriedade) 
    que utilizam uma chave de tamanho λ arbitrariamente grande para transformar
    blocos de texto/criptograma com tamanho B (muito menor que λ) numa permutação da ordem de 2^λ. 
    Qualquer adversário limitado computacionalmente, não consegue distinguir a cifra por blocos de uma permutação puramente aleatória.
    Uma cifra por blocos segura não pode ter o tamanho dos blocos de texto B demasiado pequeno.
    O tamanho de bloco deve ser suficientemente grande para que o adversário não consiga calcular todas as permutações do espetro de permutações.
    Um tamanho B seguro é um tamanho que demoraria séculos a calcular todas as permutações possíveis.

2 - What is the most widely used block cipher and why?

    A cifra por blocos mais utilizada atualmente é a AES (Advanced Encryption Standard) que utiliza um tamanho de bloco B = 128 bits.
    Foi a cifra que veio substituir a cifra insegura DES (Data Encryption Standard, com B = 64bits). 
    Ganhou a competição do ano 2000 da empresa NIST por ter a melhor performance e ser a mais resiliente.
    Como é a cifra utilizada por toda a comunidade, os CPUs já vêm equipados com circuitos próprios para a cifra AES.
    Esta implementação de Hardware, veio pelo facto de o AES ser um algoritmo muito complexo em termos de software.
    Isto permite que o AES tenha uma boa performance de implementação sem ser necessária a implementação em software.
    Até ao momento, ainda ninguém (que se saiba) conseguiu quebrar esta cifra.

3 - What is symmetric encryption and why is it different from a block cipher?
    
    As cifras simétricas são cifras que utilizam as cifras por blocos (permutações pseudo-aleatórias seguras) de forma
    a obter cifras seguras. 
    O algoritmo de cifração que as cifras simétricas utilizam para criar criptogramas utilizam aleatoriedade, logo
    qualquer bloco de texto repetido terá um criptograma diferente.
    A cifra por blocos é uma primitiva de criptografia e é com base nisto que se aplicam cifrações. No entanto,
    cifra por blocos nao é um sinónimo de segurança como são as cifras simétricas, isto porque,
    não existe aleatoriedade nas cifrações. Qualquer cifra que utiliza um algoritmo determinístico é uma cifra
    insegura porque blocos de texto iguais vão ter criptogramas iguais.

4 - Nonce-based encryption is deterministic, and yet we know of schemes that are IND-CPA secure. Give an example
and explain why this does not contradict the fact that all deterministic encryption schemes are IND-CPA insecure.
    
    As cifrações baseadas em Nonces são cifras de estados e deixam de ser cifras probabílisticas.
    Mesmo assim, são cifras seguras para o modelo IND-CPA caso nunca se repita o mesmo nonce para cifrar blocos de texto limpo.
    Como exemplo, numa aplicação temos que guardar todos os nonces utilizados por cada utilizador. Caso um utilizador 
    consiga cifrar textos com o mesmo nonce, vai obter criptogramas repetidos e daí vem o perigo para a chave da cifra.
    Logo, cada sessão do utilizador terá um nonce novo e nunca repetido e cada bloco cifrado terá um contador novo.
    É a concatenação do nonce e do contador que criamos uma permutação pseudo-aleatória que funcionará como chave da cifra.


|-------------------------|
|                         |
|      Guião Prático      |
|                         |
|-------------------------|

1 - Use Python to encrypt a file in CBC mode (follow the example in Week 2 tutorial)

    python3 week4_cbc.py

2 - Decrypt the file with OpenSSL and check for success

    openssl enc -aes-128-cbc -d -nopad -in ciphertext.bin -out ciphertext.new -K 'key_value' -iv 'iv_value'


3 - Edit the file to change the value of (but not delete!) one byte and decrypt again.
 
    edição no último byte

 • What happened?

    o último bloco não foi decifrado corretamente, enquanto que todos os outros foram.

 • Could you recover a file encrypted with CBC if the IV and the first ciphertext block were corrupted or lost?

    Não! sem o IV e sem o primeiro bloco de criptograma nunca vamos conseguir recuperar o primeiro bloco de texto limpo.

 • Could you recover it if during a satellite transmission one bit of the ciphertext is not delivered?

    Não! É preciso que todos os bits sejam entregues senão iremos ter falhas de tamanho dos blocos quando fizermos os XORs
    de reversão.

 • Could you modify a byte in the middle of a CBC encrypted file without fully re-encrypting it?
   
   Não! Se modificarmos um byte no meio de um ficheiro cifrado, os próximos blocos de criptograma vão ser afetados e 
   não vamos conseguir decifrar o documento a partir do bloco que modificamos.

4 - Repeat the exercise with CTR mode. What are the differences?
    
    Diferenças:
	  Alterações no ficheiro cifrado apenas afetam um caracter e não um bloco inteiro de texto.
	  Se houver modificações no meio do ficheiro, tudo o que não foi alterado daí para a frente,
	  não irá sofrer alterações.

    4.1) Use Python to encrypt a file in CTR mode (follow the example in Week 2 tutorial)
    
         python3 week4_ctr.py
    
    4.2) Decrypt the file with OpenSSL and check for success
         
         openssl enc -aes-128-ctr -d -nopad -in ciphertext.bin -out ciphertext.new -K 'key_value' -iv 'iv_value'
    
    4.3) Edit the file to change the value of (but not delete!) one byte and decrypt again.
         
         edição no último byte
         
         • What happened?
           
           O último caracter muda, tudo o resto fica decifrado normalmente.
         
         • Could you recover a file encrypted with CTR if the IV and the first ciphertext block were corrupted or lost?

           Não! Sem o IV nunca vamos saber a stream de chaves para cifrar o texto limpo.

         • Could you recover it if during a satellite transmission one bit of the ciphertext is not delivered?

           Não! É preciso que todos os bits sejam entregues senão iremos ter falhas de tamanho dos blocos 
           quando fizermos os XORs de reversão.

         • Could you modify a byte in the middle of a CTR encrypted file without fully re-encrypting it?

           Sim! Se soubermos o IV podemos reconstruir o texto limpo do ficheiro. Um dos blocos irá falhar na reconstrução, 
           e com isso identificamos o caracter de texto cifrado que foi modificado e procedemos a um ato de
           brute force nesse bloco.



