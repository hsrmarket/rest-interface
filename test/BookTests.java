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
public class BookTests extends WithApplication {

    public static Integer BOOKID;

    @Test
    public void TestA_PostBookTest(){

        String body = "{\"name\":\"JUnitBook\", \"price\":15, \"condition\":5, \"description\":\"This book was created with the JUnit Test\",\"creationDate\":\"2017-04-21\",\"image\":\"test.png\",\"isbn\":\"5558-34834-3453-34534\",\"author\":\"JUnit\", \"publisher\":\"JUnit\", \"type\":\"book\", \"createdby\":12}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("POST")
                .bodyJson(jsonNode)
                .uri("/api/articles");
        Result result = route(request);

        //get inserted ID for further processing
        String answerString = contentAsString(result);
        JsonNode answerJson = Json.parse(answerString);
        BOOKID = answerJson.findPath("id").intValue();

        System.out.println(BOOKID.toString());

        assertEquals(OK,result.status());
    }

    @Test
    public void TestB_GetAllBooksTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.BookController.getAllBooks().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestC_GetOneArticleTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/articles/"+ BOOKID.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestD_UpdateBookTest(){

        String body = "{\"name\":\"JUnitBook\", \"price\":15, \"condition\":5, \"description\":\"This book was updated with the JUnit Test\",\"creationDate\":\"2017-04-21\",\"image\":\"test.png\",\"isbn\":\"5558-34834-3453-34534\",\"author\":\"JUnit\", \"publisher\":\"JUnit\", \"type\":\"book\", \"createdby\":12}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("PUT")
                .bodyJson(jsonNode)
                .uri("/api/articles/"+ BOOKID.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void TestE_DeleteBookTest(){

        Http.RequestBuilder request = new Http.RequestBuilder().method("DELETE")
                .uri("/api/articles/"+ BOOKID.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

}
