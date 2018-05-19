/**
 * 
 */
package me.benzo.db.blockchain.service.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.benzo.db.blockchain.model.Block;
import me.benzo.db.blockchain.model.Node;
import me.benzo.db.blockchain.model.Transaction;

/**
 * @author xwcj427
 *
 */
@Service
@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PUBLIC)
public class BlockChain {
	
	
	private String nodeId;
	private List<Transaction> currentTransactions = new ArrayList<>();
	private List<Block> chain = new ArrayList<>();
	private List<Node> nodes = new ArrayList<>();
	@Setter(AccessLevel.PUBLIC)
	private Block lastBlock;


	// ctor
	public BlockChain() {
		this.nodeId = UUID.randomUUID().toString().replaceAll("-", "");
		try {
			this.lastBlock = createNewBlock(100, "1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	// private functionality
	public void registerNode(String address) throws MalformedURLException {
		this.nodes.add(new Node(new URL(address)));
	}

	public boolean isValidChain(List<Block> chain) throws NoSuchAlgorithmException {
		Block block = null;
		this.lastBlock = chain.get(0);
		int currentIndex = 1;
		while (currentIndex < chain.size()) {
			block = chain.get(currentIndex);

			// Check that the hash of the block is correct
			if (block.getPreviousHash() != getHash(this.lastBlock)) {
				return false;
			}

			// Check that the Proof of Work is correct
			if (!this.isValidProof(this.lastBlock.getProof(), block.getProof(), this.lastBlock.getPreviousHash())) {
				return false;
			}

			this.lastBlock = block;
			currentIndex++;
		}

		return true;
	}

	public boolean resolveConflicts() throws UnirestException, MalformedURLException, NoSuchAlgorithmException {
		List<Block> newChain = null;
		// int maxLength = this.chain.size();

		for (Node node : this.nodes) {
			URL url = new URL(node.getAddress(), "/chain");
			GetRequest request = Unirest.get(url.toString());

			HttpResponse<JsonNode> response = request.asJson();

			if (response.getStatus() == 200) {
				JSONObject json = response.getBody().getObject();
				JSONArray jsonChain = json.getJSONArray("chain");
				List<Block> blocks = Block.fromJsonArray(jsonChain);

				if (blocks.size() > this.chain.size() && isValidChain(blocks)) {
					// maxLength = blocks.size();
					newChain = blocks;
				}
			}
		}

		if (newChain != null) {
			this.chain = newChain;
			return true;
		}

		return false;
	}

	public Block createNewBlock(int proof, String previousHash) throws NoSuchAlgorithmException {
		Block block = new Block();
		block.setIndex(this.chain.size());
		block.setTimestamp(new Timestamp(new Date().getTime()));
		block.setTransactions(this.currentTransactions);
		block.setProof(proof);
		block.setPreviousHash(previousHash != null ? previousHash : getHash(this.chain.get(this.chain.size() - 1)));

		this.currentTransactions = new ArrayList<>();
		this.chain.add(block);
		return block;
	}

	public int createProofOfWork(int lastProof, String previousHash) throws NoSuchAlgorithmException {
		int proof = 0;
		while (!isValidProof(lastProof, proof, previousHash)) {
			proof++;
		}
		return proof;
	}

	public boolean isValidProof(int lastProof, int proof, String previousHash) throws NoSuchAlgorithmException {
		String guess = String.format("%s%s%s", lastProof, proof, previousHash);
		String result = getSha256(guess);
		return result.startsWith("0000");
	}

	private String getHash(Block block) throws NoSuchAlgorithmException {
		String blockText = JSONObject.valueToString(block);
		return getSha256(blockText);
	}

	private String getSha256(String data) throws NoSuchAlgorithmException {
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		StringBuilder hashBuilder = new StringBuilder();

		byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
		byte[] hash = sha256.digest(bytes);

		for (byte x : hash) {
			hashBuilder.append(String.format("%02X", x));
		}

		return hashBuilder.toString();
	}
}
