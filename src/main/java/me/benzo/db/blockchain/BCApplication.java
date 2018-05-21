/**
 * 
 */
package me.benzo.db.blockchain;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.thorntail.Thorntail;

/**
 * @author blaffitte
 *
 */
@ApplicationPath("/")
public class BCApplication extends Application {

    public static void main(String[] args) throws Exception {
        Thorntail.run();
    }
}
