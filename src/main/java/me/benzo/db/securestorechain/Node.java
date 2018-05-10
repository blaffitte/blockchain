/**
 * 
 */
package me.benzo.db.securestorechain;

import java.net.URL;

import lombok.Data;

/**
 * @author xwcj427
 *
 */
@Data
public class Node {

	private URL address;

	public Node(URL address) {
		this.address = address;
	}

}
