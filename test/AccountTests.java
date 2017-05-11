import com.fasterxml.jackson.databind.JsonNode;
import controllers.routes;
import org.junit.FixMethodOrder;
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

    @Test
    public void TestA_PostAccountTest(){
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

        //get inserted ID for further processing
        String answerString = contentAsString(result);
        JsonNode answerJson = Json.parse(answerString);
        ACCOUNTID = answerJson.findPath("id").intValue();

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
                "    \"password\": \"evil is not good\",\n" +
                "    \"admin\": false\n" +
                "  }";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("PUT")
                .bodyJson(jsonNode)
                .uri("/api/accounts");
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
