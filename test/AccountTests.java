import com.fasterxml.jackson.databind.JsonNode;
import controllers.routes;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountTests extends WithApplication {

    public static Integer ACCOUNTID;
    public static Integer ADDRESSID;

    @Test
    public void TestA_PostAccountTest(){
        String body = "{\n" +
                "    \"studentId\": 9999,\n" +
                "    \"firstname\": \"JUnit\",\n" +
                "    \"lastname\": \"Junit\",\n" +
                "    \"address\": {\n" +
                "      \"street\": \"I don't care\",\n" +
                "      \"streetNr\": \"98\",\n" +
                "      \"zip\": 7680,\n" +
                "      \"city\": \"Greensboro\"\n" +
                "    },\n" +
                "    \"email\": \"blabla@blubb.com\",\n" +
                "    \"telephone\": \"0456781234\",\n" +
                "    \"password\": \"test33test\",\n" +
                "    \"admin\": false\n" +
                "}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("POST")
                .bodyJson(jsonNode)
                .uri("/api/accounts");
        Result result = route(request);

        //get inserted ID for further processing
        String answerString = contentAsString(result);
        JsonNode answerJson = Json.parse(answerString);
        ACCOUNTID = answerJson.findPath("id").intValue();
        ADDRESSID = answerJson.get("address").findPath("id").intValue();

        System.out.println(ACCOUNTID.toString());

        assertEquals(OK,result.status());
    }
    
    @Test
    public void TestB_GetAllAccountsTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.AccountController.getAllAccounts().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestC_GetAllAdminAccountsTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.AccountController.getAdminAccounts().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestD_GetAllNormalAccountsTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.AccountController.getNormalAccounts().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestE_GetOneAccountTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/accounts/2");
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestF_UpdateAccountTest(){
        String body = "{\n" +
                "    \"id\": " + ACCOUNTID.toString() + ",\n" +
                "    \"studentId\": 9999,\n" +
                "    \"firstname\": \"JUnit\",\n" +
                "    \"lastname\": \"Junit\",\n" +
                "    \"address\": {\n" +
                "      \"id\": " + ADDRESSID.toString() + ",\n" +
                "      \"street\": \"I do care\",\n" +
                "      \"streetNr\": \"98\",\n" +
                "      \"zip\": 7680,\n" +
                "      \"city\": \"Greensboro\"\n" +
                "    },\n" +
                "    \"email\": \"blabla@blabb.ch\",\n" +
                "    \"telephone\": \"0456781234\",\n" +
                "    \"password\": \"test44test\",\n" +
                "    \"admin\": false\n" +
                "}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("PUT")
                .bodyJson(jsonNode)
                .uri("/api/accounts/" + ACCOUNTID.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestG_DeleteAccountTest(){

        Http.RequestBuilder request = new Http.RequestBuilder().method("DELETE")
                .uri("/api/accounts/"+ ACCOUNTID.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

}
