/**
 * 
 */
package me.benzo.db.blockchain.model;

import lombok.Data;

/**
 * @author blaffitte
 *
 */
@Data
public class NewBlock {

    private int proof;
    private String previousHash;
}
