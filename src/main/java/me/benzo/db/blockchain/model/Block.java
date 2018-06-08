/**
 * 
 */
package me.benzo.db.blockchain.model;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author blaffitte
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Block {

    private int index;
    private Timestamp timestamp;
    private List<Transaction> transactions;
    private int proof;
    private String previousHash;

}
