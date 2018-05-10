/**
 * 
 */
package me.benzo.db.blockchain;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xwcj427
 *
 */
@RestController()
@RequestMapping(produces = "application/json")
public class BlockChain {
	@Getter(AccessLevel.PUBLIC)
	@Setter(AccessLevel.PRIVATE)
	private String nodeId;

	private List<Transaction> currentTransactions = new ArrayList<>();
	private List<Block> chain = new ArrayList<>();
	private List<Node> nodes = new ArrayList<>();
	private Block lastBlock;

	// ctor
	public BlockChain() {
		nodeId = UUID.randomUUID().toString().replaceAll("-", "");
		try {
			this.lastBlock = createNewBlock(100, "1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	// private functionality
	private void registerNode(String address) throws MalformedURLException {
		nodes.add(new Node(new URL(address)));
	}

	private boolean isValidChain(List<Block> chain) throws NoSuchAlgorithmException {
		Block block = null;
		lastBlock = chain.get(0);
		int currentIndex = 1;
		while (currentIndex < chain.size()) {
			block = chain.get(currentIndex);

			// Check that the hash of the block is correct
			if (block.getPreviousHash() != getHash(lastBlock)) {
				return false;
			}

			// Check that the Proof of Work is correct
			if (!this.isValidProof(lastBlock.getProof(), block.getProof(), lastBlock.getPreviousHash())) {
				return false;
			}

			lastBlock = block;
			currentIndex++;
		}

		return true;
	}

	private boolean resolveConflicts() throws UnirestException, MalformedURLException, NoSuchAlgorithmException {
		List<Block> newChain = null;
		// int maxLength = this.chain.size();

		for (Node node : nodes) {
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

	private Block createNewBlock(int proof, String previousHash) throws NoSuchAlgorithmException {
		Block block = new Block();
		block.setIndex(this.chain.size());
		block.setTimestamp(new Timestamp(new Date().getTime()));
		block.setTransactions(this.currentTransactions);
		block.setProof(proof);
		block.setPreviousHash(previousHash != null ? previousHash : getHash(chain.get(chain.size() - 1)));

		this.currentTransactions = new ArrayList<>();
		this.chain.add(block);
		return block;
	}

	private int createProofOfWork(int lastProof, String previousHash) throws NoSuchAlgorithmException {
		int proof = 0;
		while (!isValidProof(lastProof, proof, previousHash)) {
			proof++;
		}
		return proof;
	}

	private boolean isValidProof(int lastProof, int proof, String previousHash) throws NoSuchAlgorithmException {
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
	/*
	 * ===============================================================
	 * $"http://{host}:{port}/mine", <br> $"http://{host}:{port}/transactions", <br>
	 * $"http://{host}:{port}/chain", <br> $"http://{host}:{port}/nodes/register",
	 * <br> $"http://{host}:{port}/nodes/resolve"
	 * ===============================================================
	 */

	// web server calls
	@RequestMapping(value = "/mine", method = RequestMethod.POST)
	public Callable<ResponseEntity<?>> mine() throws NoSuchAlgorithmException {
		return () -> {
			int proof = createProofOfWork(this.lastBlock.getProof(), lastBlock.getPreviousHash());

			createTransaction(new Transaction(nodeId, "0", ""));
			Block block = createNewBlock(proof, null /* , _lastBlock.PreviousHash */);
			this.lastBlock = block;

			Map<String, Object> response = new HashMap<>();
			response.put("message", "New Block Forged");
			response.put("index", block.getIndex());
			response.put("transactions", block.getTransactions());
			response.put("proof", block.getProof());
			response.put("previousHash", block.getPreviousHash());

			return ResponseEntity.ok(response);
		};
	}

	@RequestMapping(value = "/chain", method = RequestMethod.GET)
	public ResponseEntity<?> fullChain() {
		Map<String, Object> response = new HashMap<>();
		response.put("chain", this.chain);
		response.put("length", this.chain.size());
		return ResponseEntity.ok(response);
	}
	
	@RequestMapping(value = "/nodes/whoami", method = RequestMethod.GET)
	public ResponseEntity<?> whoami(){
		return ResponseEntity.ok(this.getNodeId());
	}

	@RequestMapping(value = "/nodes", method = RequestMethod.GET)
	public ResponseEntity<?> listNodes(){
		return ResponseEntity.ok(this.nodes);
	}
	
	@RequestMapping(value = "/nodes/register", method = RequestMethod.POST, produces = "text/plain", consumes = "application/json")
	public String registerNodes(@RequestBody String[] nodes) throws MalformedURLException {
		StringBuilder builder = new StringBuilder();
		for (String node : nodes) {
			String url = String.format("http://%s", node);
			registerNode(url);
			builder.append(String.format("%s, ", url));
		}

		builder.insert(0, String.format("%i new nodes have been added: ", nodes.length));
		String result = builder.toString();
		return result.substring(0, result.length() - 2);

	}

	@RequestMapping(value = "/nodes/resolve", method = RequestMethod.POST)
	public ResponseEntity<?> consensus() throws MalformedURLException, NoSuchAlgorithmException, UnirestException {
		boolean replaced = resolveConflicts();
		String message = replaced ? "was replaced" : "is authoritive";

		Map<String, Object> response = new HashMap<>();
		response.put("message", String.format("Our chain %s", message));
		response.put("chain", JSONObject.valueToString(chain));

		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/transactions", method = RequestMethod.POST)
	public ResponseEntity<?> createTransaction(@RequestBody Transaction data) {
		this.currentTransactions.add(data);

		return ResponseEntity.ok(this.lastBlock != null ? lastBlock.getIndex() + 1 : 0);
	}
}
