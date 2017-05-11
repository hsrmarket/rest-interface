import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import controllers.routes;
import org.junit.Test;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import play.api.test.Helpers.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

public class BookTests extends WithApplication {

    public Integer bookId = 202;

    @Test
    public void postBookTest(){

        String body = "{\"name\":\"JUnitBook\", \"price\":15, \"condition\":5, \"description\":\"This book was created with the JUnit Test\",\"creationDate\":\"2017-04-21\",\"image\":\"test.png\",\"isbn\":\"5558-34834-3453-34534\",\"author\":\"JUnit\", \"publisher\":\"JUnit\", \"type\":\"book\", \"createdby\":12}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("POST")
                .bodyJson(jsonNode)
                .uri(routes.ArticleController.insertArticle().url());
        Result result = route(request);

        //get inserted ID for further processing
        String answerString = contentAsString(result);
        JsonNode answerJson = Json.parse(answerString);
        bookId = answerJson.findPath("id").intValue();

        assertEquals(OK,result.status());
    }

    @Test
    public void getAllBooksTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.BookController.getAllBooks().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void getOneArticleTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/articles/"+bookId.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void updateBookTest(){
        
        String body = "{\"name\":\"JUnitBook\", \"price\":15, \"condition\":5, \"description\":\"This book was updated with the JUnit Test\",\"creationDate\":\"2017-04-21\",\"image\":\"test.png\",\"isbn\":\"5558-34834-3453-34534\",\"author\":\"JUnit\", \"publisher\":\"JUnit\", \"type\":\"book\", \"createdby\":12}";

        JsonNode jsonNode = Json.parse(body);

        Http.RequestBuilder request = new Http.RequestBuilder().method("PUT")
                .bodyJson(jsonNode)
                .uri("/api/articles/"+bookId.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void deleteBookTest(){

        Http.RequestBuilder request = new Http.RequestBuilder().method("DELETE")
                .uri("/api/articles/"+bookId.toString());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

}
