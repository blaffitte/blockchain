/**
 * 
 */
package me.benzo.db.blockchain.model;

import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author blaffitte
 *
 */
@Data
@AllArgsConstructor
public class Node {

    private URL address;
    
}
