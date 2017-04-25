import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;

public class PostAccountTests extends WithApplication {

    @Test
    public void postAccountTest(){
        String body = "{\n" +
                "    \"studentId\": 98765,\n" +
                "    \"firstname\": \"JUnit\",\n" +
                "    \"lastname\": \"JUnit\",\n" +
                "    \"address\": {\n" +
                "      \"street\": \"Highway to Hell\",\n" +
                "      \"streetNr\": \"66\",\n" +
                "      \"zip\": 666,\n" +
                "      \"city\": \"Hell\"\n" +
                "    },\n" +
                "    \"email\": \"JUnit.JUnit@hell.com\",\n" +
                "    \"telephone\": \"66666666666\",\n" +
                "    \"password\": \"evil is good\",\n" +
                "    \"admin\": true\n" +
                "  }";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("POST")
                .bodyJson(jsonNode)
                .uri("/api/accounts");
        Result result = route(request);

        assertEquals(OK,result.status());
    }


    @Test
    public void postAccountWithWrongURITest(){
        String body = "{\n" +
                "    \"studentId\": 98765,\n" +
                "    \"firstname\": \"JUnit\",\n" +
                "    \"lastname\": \"JUnit\",\n" +
                "    \"address\": {\n" +
                "      \"street\": \"Highway to Hell\",\n" +
                "      \"streetNr\": \"66\",\n" +
                "      \"zip\": 666,\n" +
                "      \"city\": \"Hell\"\n" +
                "    },\n" +
                "    \"email\": \"JUnit.JUnit@hell.com\",\n" +
                "    \"telephone\": \"66666666666\",\n" +
                "    \"password\": \"evil is good\",\n" +
                "    \"admin\": true\n" +
                "  }";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("POST")
                .bodyJson(jsonNode)
                .uri("/api/accots");
        Result result = route(request);

        assertEquals(NOT_FOUND,result.status());
    }

}
