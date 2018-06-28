import org.junit.jupiter.api.Test;
import steam.QueryFilterBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryFilterBuilderTest {

    @Test
    public void runTest() {
        QueryFilterBuilder builder = new QueryFilterBuilder();
        builder.napp(500);
        assertEquals("\\napp\\500\0", builder.build());

        QueryFilterBuilder dayz = new QueryFilterBuilder();
        dayz.game("dayz");
        dayz.versionMatches("0.63*");
        assertEquals("\\gamedir\\dayz\\version_match\\0.63*\0", dayz.build());
    }

}
