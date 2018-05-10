/**
 * 
 */
package me.benzo.db.blockchain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.Data;

/**
 * @author xwcj427
 *
 */
@Data
public class Block {

	private int index;
	private Timestamp timestamp;
	private List<Transaction> transactions;
	private int proof;
	private String previousHash;

	public String toJSONString() {
		return String.format(
				"{\"index\":%i, \"timestamp\":%i, \"transactions\": %s, \"proof\": %i, \"previousHash\": \"%s\" }",
				index, timestamp.getTime(), JSONObject.valueToString(transactions), proof, previousHash);
	}

	public static Block fromJson(JSONObject json) {
		Block ret = new Block();
		ret.setIndex(json.getInt("index"));
		ret.setTimestamp(new Timestamp(json.getLong("timestamp")));
		ret.setProof(json.getInt("proof"));
		ret.setTransactions(Transaction.fromJsonArray(json.getJSONArray("transactions")));
		ret.setPreviousHash(json.getString("previousHash"));
		return ret;
	}

	public static List<Block> fromJsonArray(JSONArray array) {
		List<Block> ret = new ArrayList<>();
		array.forEach((node) -> {
			ret.add(fromJson((JSONObject) node));
		});
		return ret;
	}
}
