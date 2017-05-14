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
public class OfficeSupplyTests extends WithApplication {

    public static Integer OFFICESUPPLYID;

    @Test
    public void TestA_PostOfficeSupplyTest(){
        String body = "{\"name\":\"JUnitOfficeSupply\", \"price\":15, \"condition\":5, \"description\":\"This office supply was created with the JUnit Test\",\"creationDate\":\"2017-04-21\",\"image\":\"test.png\",\"type\":\"office supply\", \"createdby\":12}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("POST")
                .bodyJson(jsonNode)
                .uri(routes.ArticleController.insertArticle().url());
        Result result = route(request);

        //get inserted ID for further processing
        String answerString = contentAsString(result);
        JsonNode answerJson = Json.parse(answerString);
        OFFICESUPPLYID = answerJson.findPath("id").intValue();

        System.out.println(OFFICESUPPLYID.toString());

        assertEquals(OK,result.status());
    }

    @Test
    public void TestB_GetAllOfficeSuppliesTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.OfficeSupplyController.getAllOfficeSupplies().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestC_GetOneArticleTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/articles/"+ OFFICESUPPLYID.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestD_UpdateOfficeSupplyTest(){
        String body = "{\"name\":\"JUnitOfficeSupply\", \"price\":15, \"condition\":5, \"description\":\"This office supply was updated with the JUnit Test\",\"creationDate\":\"2017-04-21\",\"image\":\"test.png\",\"type\":\"office supply\", \"createdby\":12}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("PUT")
                .bodyJson(jsonNode)
                .uri("/api/articles/"+ OFFICESUPPLYID.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestE_DeleteElectronicTest(){

        Http.RequestBuilder request = new Http.RequestBuilder().method("DELETE")
                .uri("/api/articles/"+ OFFICESUPPLYID.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    
}
