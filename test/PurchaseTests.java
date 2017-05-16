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
public class PurchaseTests extends WithApplication{

    public static Integer PURCHASEID;

    @Test
    public void TestA_PostPurchaseTest(){
        String body = "{\n" +
                "  \"article\": {\n" +
                "    \"id\": 25,\n" +
                "    \"name\": \"JUnit\",\n" +
                "    \"price\": 15,\n" +
                "    \"condition\": 5,\n" +
                "    \"description\": \"This book was created with the JUnit Test\",\n" +
                "    \"creationDate\": \"2017-04-21\",\n" +
                "    \"image\": \"\",\n" +
                "    \"type\": \"book\",\n" +
                "    \"isbn\": \"5558-34834-3453-34534\",\n" +
                "    \"author\": \"JUnit\",\n" +
                "    \"publisher\": \"HSR\"\n" +
                "},\n" +
                "  \"buyer\": {\n" +
                "    \"id\": 5,\n" +
                "    \"studentId\": 167456,\n" +
                "    \"firstname\": \"Elias\",\n" +
                "    \"lastname\": \"Brunner\",\n" +
                "    \"address\": {\n" +
                "      \"id\": 2,\n" +
                "      \"street\": \"Blue Hague Boulevard\",\n" +
                "      \"streetNr\": \"49\",\n" +
                "      \"zip\": 671,\n" +
                "      \"city\": \"Honolulu\"\n" +
                "    },\n" +
                "    \"email\": \"elias.brunner@hsr.ch\",\n" +
                "    \"telephone\": \"0978563456\",\n" +
                "    \"password\": \"test33test\",\n" +
                "    \"admin\": true\n" +
                "  },\n" +
                "  \"purchaseDate\": \"2017-05-08\",\n" +
                "  \"completed\": false\n" +
                "}";
        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("POST")
                .bodyJson(jsonNode)
                .uri("/api/purchases");
        Result result = route(request);

        //get inserted ID for further processing
        String answerString = contentAsString(result);
        JsonNode answerJson = Json.parse(answerString);
        PURCHASEID = answerJson.findPath("id").intValue();

        System.out.println(PURCHASEID.toString());

        assertEquals(OK,result.status());
    }

    @Test
    public void TestB_GetAllPurchasesTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.PurchaseController.getAllPurchases().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestC_GetOnePurchaseTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/purchases/"+ PURCHASEID.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestD_UpdatePurchaseTest(){

        String body = "{\n" +
                "\t\"completed\": false\n" +
                "}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("PATCH")
                .bodyJson(jsonNode)
                .uri("/api/purchases/" + PURCHASEID.toString());
        Result result = route(request);

        assertEquals(OK,result.status());


    }

    @Test
    public void TestE_DeletePurchaseTest(){

        Http.RequestBuilder request = new Http.RequestBuilder().method("DELETE")
                .uri("/api/purchases/"+ PURCHASEID.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }


}
