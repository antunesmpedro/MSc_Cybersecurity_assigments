package blockchain;

import java.security.MessageDigest;

public class Hash_helper {
	public static String sha256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < hash.length; i++){
				String hex = Integer.toHexString(0xff & hash[i]); // '0xff & hash[i] set bits result and then converted to decimal'
				if (hex.length() == 1)	// add leading zero if needed (all hex conversion sets with 2 chars)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}