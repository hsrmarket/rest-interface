import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;

public class GetOneAccountTests extends WithApplication {

    @Test
    public void getOneArticleTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/accounts/2");
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void getOneArticleWithNonExistingIDTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/accounts/0");
        Result result = route(request);

        assertEquals(BAD_REQUEST,result.status());
    }

    @Test
    public void getOneArticleWithWrongURI(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/accnts/2");
        Result result = route(request);

        assertEquals(NOT_FOUND,result.status());
    }


}
