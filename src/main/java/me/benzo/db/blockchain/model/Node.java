/**
 *
 */
package me.benzo.db.blockchain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URL;

/**
 * @author blaffitte
 *
 */
@Data
@AllArgsConstructor
public class Node {

    private URL address;

}
