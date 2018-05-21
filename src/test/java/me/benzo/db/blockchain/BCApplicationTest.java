package me.benzo.db.blockchain;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.thorntail.test.ThorntailTestRunner;

@RunWith(ThorntailTestRunner.class)
public class BCApplicationTest {
    @Test
    public void test() {
        when().get("/chain").then().statusCode(200).body(containsString("chain"));
    }
}
