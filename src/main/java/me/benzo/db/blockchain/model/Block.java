/**
 * 
 */
package me.benzo.db.blockchain.model;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.Data;

/**
 * @author blaffitte
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

	public static Block fromJson(JSONObject json) throws JSONException, ParseException {
		Block ret = new Block();
		ret.setIndex(json.getInt("index"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	    Date parsedDate = dateFormat.parse(json.getString("timestamp"));
	    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
		ret.setTimestamp(timestamp);
		ret.setProof(json.getInt("proof"));
		ret.setTransactions(Transaction.fromJsonArray(json.getJSONArray("transactions")));
		ret.setPreviousHash(json.getString("previousHash"));
		return ret;
	}

	public static List<Block> fromJsonArray(JSONArray array) {
		List<Block> ret = new ArrayList<>();
		array.forEach((node) -> {
			try {
				ret.add(fromJson((JSONObject) node));
			} catch (ParseException | JSONException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		});
		return ret;
	}
}
