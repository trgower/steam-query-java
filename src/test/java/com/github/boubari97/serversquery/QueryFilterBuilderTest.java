package com.github.boubari97.serversquery;

import org.junit.jupiter.api.Test;
import com.github.boubari97.serversquery.queries.QueryFilterBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryFilterBuilderTest {

    @Test
    void fullBuilderTest() {
        QueryFilterBuilder builder = new QueryFilterBuilder();
        String expectedResult = "\\nor\\nand\\dedicated\\1\\secure\\1\\gamedir\\game" +
                "\\map\\map\\linux\\1\\password\\0\\empty\\1\\full\\1\\proxy\\1\\appid\\1" +
                "\\napp\\1\\noplayers\\1\\white\\1\\gametype\\type\\gamedata\\data" +
                "\\gamedataor\\or\\name_match\\matches\\name_match\\*name*" +
                "\\version_match\\1.0\\collapse_addr_hash\\1\\collapse_addr_hash\\0.0.0.0\0";

        String actualResult = builder.nor().nand().dedicated().secure().game("game")
                .map("map").linux().password().notEmpty().notFull().proxy().appId(1)
                .napp(1).isEmpty().white().gameType("type").gameData("data")
                .gameDataOr("or").nameMatches("matches").searchMatcherByNameExist("name")
                .versionMatches("1.0").unique().addressMatches("0.0.0.0").build();

        assertEquals(expectedResult, actualResult);
    }

}
