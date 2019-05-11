/**
 *
 */
package me.benzo.db.blockchain.model;

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
    private String sender;
    private String recipient;
    private String data;
}
