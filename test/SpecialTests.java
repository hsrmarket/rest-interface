import com.fasterxml.jackson.databind.JsonNode;
import controllers.routes;
import org.junit.Ignore;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;


public class SpecialTests extends WithApplication {

    @Test
    public void postBookWithoutTypeTest(){
        String body = "{\"name\":\"JUnitBook\", \"price\":15, \"condition\":5, \"description\":\"This book was created with the JUnit Test\",\"creationDate\":\"2017-04-21\",\"image\":\"test.png\",\"isbn\":\"5558-34834-3453-34534\",\"author\":\"JUnit\", \"publisher\":\"JUnit\", \"createdby\":12}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("POST")
                .bodyJson(jsonNode)
                .uri(routes.ArticleController.insertArticle().url());
        Result result = route(request);

        assertEquals(BAD_REQUEST,result.status());
    }

    @Test
    public void postTypeThatNotExistsTest(){
        String body = "{\"name\":\"JUnitOfficeSupply\", \"price\":15, \"condition\":5, \"description\":\"This office supply was created with the JUnit Test\",\"creationDate\":\"2017-04-21\",\"image\":\"test.png\",\"type\":\"this type does not exist\", \"createdby\":12}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("POST")
                .bodyJson(jsonNode)
                .uri(routes.ArticleController.insertArticle().url());
        Result result = route(request);

        assertEquals(BAD_REQUEST,result.status());
    }

    @Test
    public void postOtherArticleWithWrongURITest(){
        String body = "{\"name\":\"JUnitOtherArticle\", \"price\":15, \"condition\":5, \"description\":\"This other Article was created with the JUnit Test\",\"creationDate\":\"2017-04-21\",\"image\":\"test.png\",\"type\":\"other\", \"createdby\":12}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("POST")
                .bodyJson(jsonNode)
                .uri("/api/artices");
        Result result = route(request);

        assertEquals(NOT_FOUND,result.status());
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

    @Test
    public void getAllBooksTestWithWrongURI(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/artics/books");
        Result result = route(request);

        assertEquals(NOT_FOUND,result.status());
    }

    @Test
    public void getOneArticleWithNonExistingIDTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/articles/0");
        Result result = route(request);

        assertEquals(BAD_REQUEST,result.status());
    }

    @Test
    public void getOneArticleWithWrongURI(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/articl/3");
        Result result = route(request);

        assertEquals(NOT_FOUND,result.status());
    }

    @Test
    public void getAllAccountsTestWithWrongURI(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/accnts");
        Result result = route(request);

        assertEquals(NOT_FOUND,result.status());
    }

    @Test
    public void getOneAccountWithNonExistingIDTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/accounts/0");
        Result result = route(request);

        assertEquals(BAD_REQUEST,result.status());
    }

    @Test
    public void getOneAccountWithWrongURI(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/accnts/2");
        Result result = route(request);

        assertEquals(NOT_FOUND,result.status());
    }

    @Test
    public void findArticleBySearching(){

        String body = "{\n" +
                "    \"search\": \"test\"\n" +
                "}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("POST")
                .bodyJson(jsonNode)
                .uri("/api/search");
        Result result = route(request);

        assertEquals(OK,result.status());
    }
}
