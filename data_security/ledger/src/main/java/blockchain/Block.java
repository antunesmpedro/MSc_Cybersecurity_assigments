package blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.security.*;

// consists blocks
// Blocks consists Transaction
// Blocks are connected through hashing
	// unique digital fingerprint - transaction + previous blocks fingerprint

public class Block  {
	private final int NUMBER_OF_NONCE_BYTES = 4;

	public String hash ;
	public String prev_hash ;
	private long timestamp ;
	private double nonce;
	private String root_merkle_hash;
	private List<Transaction> transactions;



	public Block(String prevHash) {
		this.prev_hash = prevHash ;
		this.timestamp = new Date().getTime();
		this.hash = calculateHash();

		this.transactions = new ArrayList<>();
		this.nonce = 0;
		this.root_merkle_hash= "";
	}

	private void setRoot_merkle_hash() {
		List<String> tree = merkleTree();
		this.root_merkle_hash = tree.get(tree.size()-1);
	}

	private List<String> merkleTree(){
		List<String> tree = new ArrayList<>();

		// adicionar hashes das transacoes à árvore
		for ( Transaction t : transactions)
			tree.add(t.getHash());

		int levelOffSet = 0; // contador de nos visitados. offset para saber onde começa cada nível.

		for ( int size_level = transactions.size(); size_level > 1; size_level = (size_level+1) / 2 ) { // nmr de nos por nivel. prox nivel tem o nmr de nos = (size_level+1)/2
			for ( int left = 0 ; left < size_level ; left +=2 ) { //percorre todos os nos da esquerda de cada nivel
				int right = Math.min( left+1 , size_level-1 );		// right=min(left+1, size_level-1) (caso o nmr de transactions seja impar, right=left)
				String left_hash = tree.get(levelOffSet + left);
				String right_hash = tree.get(levelOffSet + right);
				tree.add( Hash_helper.sha256( left_hash + right_hash )); //add num novo nivel H(left_hash+right_hash)
			}
			levelOffSet += size_level; // nos lidos neste nivel, para que o proximo nivel saber que começa a partir deste valor.
		}

		return tree;
	}

	// calculate hash of this block
	public String calculateHash() {

		// hash do cabecalho do bloco = prev_hash + nonce + timestamp + root_merkle_hash
		String hash_block = Hash_helper.sha256(
				prev_hash +
				timestamp +
				nonce +
				root_merkle_hash
		);

		return hash_block;
	}

	// nonce challenge to calculate hash of this block
	public void mineBlock(int difficulty) throws NoSuchAlgorithmException {

		//Create a string with difficulty * "0"
		String hash_prefix = new String(new char[difficulty]).replace('\0','0');

		while ( !hash.substring(0,difficulty).equals(hash_prefix)) {
			// strong RNG generates random numbers with NO_OF_NONCE_BYTES bytes length
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(System.currentTimeMillis() % 1000);
			byte[] ranBytes = new byte[NUMBER_OF_NONCE_BYTES];
			random.nextBytes(ranBytes);

			for(int i=0; i<ranBytes.length; i++) {
				int number = 0xff & ranBytes[i];
				nonce += (double) number;
				// o ciclo para caso receba a hash calculdada de outro miner. depois verifica se a hash esta correta
			}

			hash = calculateHash();
		}
		System.out.println("Block Mined!! : " + hash);
	}

	public boolean isTransactionsValid() {
		List<String> tree = merkleTree();

		int size = tree.size();
		if(size == 0)
			return true;	// genesis_block case
		else {
			String root = tree.get( size - 1 );
			return root.equals(this.getRootMerkleHash());
		}
	}

	public boolean isFull() {
		//if ( transactions == null )
			//return false;	// genesis_block case

		if ( transactions.size() < 5 )
			return false;

		return true;
	}

	// true if t is added successfully ;
	// false tells that this block is ready to be mined
	public boolean addTransaction(Transaction t) {
		if ( !isFull() ) {
			transactions.add(t);
			setRoot_merkle_hash();
			return true;
		}
		return false;
	}

	//
	// GETTERS
	//

	public String getRootMerkleHash() {
		return root_merkle_hash;
	}

	public long getTimestamp(){
		return timestamp;
	}

	public List<Transaction> getTransactions(){
		return transactions;
	}
} 
