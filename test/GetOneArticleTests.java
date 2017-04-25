import controllers.routes;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.*;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

public class GetOneArticleTests extends WithApplication {

    @Test
    public void getOneArticleTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/articles/3");
        Result result = route(request);

        assertEquals(OK,result.status());
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

}
