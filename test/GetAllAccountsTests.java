import controllers.routes;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;

public class GetAllAccountsTests extends WithApplication{

    @Test
    public void getAllAccountsTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.AccountController.getAllAccounts().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void getAllAdminAccountsTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.AccountController.getAdminAccounts().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void getAllNormalAccountsTest(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri(routes.AccountController.getNormalAccounts().url());
        Result result = route(request);

        assertEquals(OK,result.status());
    }

    @Test
    public void getAllAccountsTestWithWrongURI(){
        Http.RequestBuilder request = new Http.RequestBuilder().method("GET")
                .uri("/api/accnts");
        Result result = route(request);

        assertEquals(NOT_FOUND,result.status());
    }

}
