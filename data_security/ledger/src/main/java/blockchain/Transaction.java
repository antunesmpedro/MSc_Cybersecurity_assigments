package blockchain;

import java.lang.Object;
import java.util.Date;

public class Transaction extends Object{
	private String buyer ;
	private String seller ;
	private int amount ;
	private String hash;
	private long timestamp;

	public Transaction(String buyer, int amount, String seller) {
		this.buyer = buyer ;
		this.seller = seller ;
		this.amount = amount ;
		this.timestamp = new Date().getTime();;


		String digest = timestamp + ":" + buyer + "," + String.valueOf(amount) + "," + seller;

		this.hash = Hash_helper.sha256(digest);
	}
	//
	// GETTERS
	//

	public String getHash(){
		return hash;
	}

	public String getBuyer(){
		return buyer;
	}

	public String getSeller(){
		return seller;
	}

	public int getAmount(){
		return amount;
	}


} 