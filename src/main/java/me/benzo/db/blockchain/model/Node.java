/**
 * 
 */
package me.benzo.db.blockchain.model;

import java.net.URL;

import lombok.Data;

/**
 * @author blaffitte
 *
 */
@Data
public class Node {

    private URL address;

    public Node(URL address) {
        this.address = address;
    }

}
