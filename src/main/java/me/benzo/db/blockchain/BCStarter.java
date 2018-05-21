/**
 * 
 */
package me.benzo.db.blockchain;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
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

        /* @formatter:off */
		File[] deps = Maven.resolver()
		                   .loadPomFromFile("pom.xml")
				           .resolve("com.mashape.unirest:unirest-java", 
				                    "org.projectlombok:lombok")
				           .withTransitivity()
				           .asFile();
		/* @formatter:on */

        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "blockchain.war");
        deployment.addPackages(true, "me/benzo/db");
        deployment.addAsLibraries(deps);

        swarm.deploy(deployment);
    }
}
