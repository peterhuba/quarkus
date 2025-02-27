package org.jboss.resteasy.reactive.server.vertx.test.response;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNull.nullValue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.vertx.test.framework.ResteasyReactiveUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class ContentLengthTest {

    private static final int NUMBER_OF_COPIES = 500;

    @RegisterExtension
    static ResteasyReactiveUnitTest runner = new ResteasyReactiveUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(FileResource.class));

    @Test
    void testResponseHeaders() {
        when()
                .get("/file")
                .then()
                .statusCode(200)
                .header(HttpHeaders.CONTENT_LENGTH,
                        greaterThan(
                                "" + (NUMBER_OF_COPIES * UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8).length)))
                .header("transfer-encoding", nullValue());
    }

    @Path("/file")
    public static class FileResource {

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public Response hello() {
            byte[] bytes = String.join(";", Collections.nCopies(NUMBER_OF_COPIES, UUID.randomUUID().toString()))
                    .getBytes(StandardCharsets.UTF_8);
            return Response.ok(new ByteArrayInputStream(bytes), "text/plain")
                    .header(HttpHeaders.CONTENT_LENGTH, bytes.length)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = " + "uuid.txt")
                    .build();
        }
    }
}
