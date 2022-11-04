package blockchain;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.GsonBuilder;

public class BlockChain {
	public static int difficulty = 4;
	public static ArrayList<Block> blockchain = new ArrayList<>();

	public static boolean isChainValid(){
		Block curr_block;
		Block prev_block;

		//Create a string with difficulty * "0"
		String hash_prefix = new String(new char[difficulty]).replace('\0','0');
		//check hashes of each block of blockchain
		for ( int i=1; i < blockchain.size(); i++ ) {
			curr_block = blockchain.get(i);
			prev_block = blockchain.get(i-1);

			//compare previous hash and registered previous hash
			if ( !prev_block.hash.equals(curr_block.prev_hash) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			// check if hash has been mined
			if ( !curr_block.hash.substring(0, difficulty).equals(hash_prefix) ) {
				System.out.println("This block hasn't been mined");

				//calculate hash again and compare with registered hash
				if ( !curr_block.hash.equals(curr_block.calculateHash()) ){
					System.out.println("Current Hashes not equal");
					return false;
				}
				return false;
			}
			// check if transaction have integrity
			if ( !curr_block.isTransactionsValid() )
				return false;
		}
		return true;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		int curr = 0;
		List<Transaction> transactions_list = new ArrayList<>();

		transactions_list.add(new Transaction("Bob", 1, "Pedro"));
		transactions_list.add(new Transaction("Bob", 1, "Alice"));
		transactions_list.add(new Transaction("Pedro", 5, "Alice"));
		transactions_list.add(new Transaction("Pedro", 1, "Bob"));
		transactions_list.add(new Transaction("Alice", 15, "Bob"));
		transactions_list.add(new Transaction("Alice", 1, "Pedro"));

		// GENESIS BLOCK
		Block genesis_block = new Block("0");
		Block current_block = genesis_block;

		// INPUT TEST: insert twice transaction_list
		for(int i=0; i<2; i++) {
			for ( Transaction t : transactions_list ) {
				//Block current_block = blockchain.get(curr);

				boolean success_transaction_block = current_block.addTransaction(t);

				// check if block is ready to mine
				if ( current_block.isFull() || !success_transaction_block ) {
					if ( current_block.isTransactionsValid() ) {	// can i trust in this block transactions?

						// mine current block
						System.out.println("Trying to Mine block " + (curr + 1) + "... ");
						current_block.mineBlock(difficulty);
						blockchain.add(current_block);

						if (!BlockChain.isChainValid()) {
							System.out.println("\n\n\nFRAUD DETECTED ON BLOCKCHAIN");
							break;
						}

						// create new block
						Block new_block = new Block(current_block.hash);
						curr++;
						current_block = new_block;
					}
				}
			}
		}


		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
	}
}