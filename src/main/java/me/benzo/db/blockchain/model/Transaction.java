/**
 * 
 */
package me.benzo.db.blockchain.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author blaffitte
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

	private String recipient;
	private String sender;
	private String data;

	public String toJSONString(String string, String nodeId, String string2) {
		return String.format("{\"recipient\":\"%s\", \"sender\":\"%s\", \"data\": \"%s\"}", recipient, sender, data);
	}

	public static Transaction fromJson(JSONObject node) {
		Transaction ret = new Transaction();
		ret.setData(node.getString("data"));
		ret.setSender(node.getString("sender"));
		ret.setRecipient(node.getString("recipient"));
		return ret;
	}

	public static List<Transaction> fromJsonArray(JSONArray array) {
		List<Transaction> ret = new ArrayList<>();
		array.forEach((node) -> {
			ret.add(fromJson((JSONObject) node));
		});
		return ret;
	}
}
