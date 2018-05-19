/**
 * 
 */
package me.benzo.db.blockchain;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

/**
 * @author blaffitte
 *
 */
public class BCStarter {

	public static void main(String... args) throws Exception {
		Swarm swarm = new Swarm(args);
		swarm.start();

		JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "blockchain.war");
		deployment.addPackages(true, "me/benzo/db");
		deployment.addAllDependencies();
		

		swarm.deploy(deployment);
	}
}
