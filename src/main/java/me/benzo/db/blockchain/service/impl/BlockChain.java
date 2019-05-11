/**
 *
 */
package me.benzo.db.blockchain.service.impl;

import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.benzo.db.blockchain.model.Block;
import me.benzo.db.blockchain.model.Node;
import me.benzo.db.blockchain.model.Transaction;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author blaffitte
 *
 */

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PUBLIC)
public class BlockChain {

    private String nodeId;
    private List<Transaction> currentTransactions = new ArrayList<>();
    private List<Block> chain = new LinkedList<>();
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

    public void registerNode(String address) throws MalformedURLException {
        this.nodes.add(new Node(new URL(address)));
    }

    public void createTransaction(final Transaction data) {
        this.getCurrentTransactions().add(data);
    }

    public void mine() throws NoSuchAlgorithmException {
        int proof = this.createProofOfWork(this.getLastBlock().getProof(), this.getLastBlock().getPreviousHash());

        this.createTransaction(new Transaction(this.getNodeId(), "0", ""));
        Block block = this.createNewBlock(proof, null /* , _getLastBlock().PreviousHash */);
        this.setLastBlock(block);
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


    public Block createNewBlock(int proof, String previousHash) throws NoSuchAlgorithmException {
        Block block = new Block();
        block.setIndex(this.chain.size());
        block.setTimestamp(new Timestamp(new Date().getTime()));
        // block.setContents(JsonObject.mapFrom(this.currentTransactions));
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
        String blockText = JsonObject.mapFrom(block).encode();
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
