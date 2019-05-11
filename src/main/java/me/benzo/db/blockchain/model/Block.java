/**
 *
 */
package me.benzo.db.blockchain.model;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

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
    private List<JsonObject> contents;
    private int proof;
    private String previousHash;

}
