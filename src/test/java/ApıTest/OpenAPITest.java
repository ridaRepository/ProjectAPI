package ApıTest;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utilities.APIUtils;
import utilities.ConfigurationReader;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class OpenAPITest {


    @BeforeClass
    public void beforeclass(){
        baseURI= ConfigurationReader.get("api_url");
    }
    String url = "https://api.mocki.io/v1/4862d8e7";

    /*
    TC:should return notifications for the following countries: BR, AR
     */
    @Test
    public void getCountries(){

        given().accept(ContentType.JSON)
                .and()
                .get(url)
                .then().assertThat()
                .statusCode(200)
                .and().assertThat()
                .contentType("application/json; charset=utf-8")
                .and().header("etag",notNullValue())
                .assertThat()
                .header("Connection",equalTo("keep-alive"));

        Response response =get();
        JsonPath jsonPath= response.jsonPath();

        for (int i=0; i<5 ; i++){
            String Country = jsonPath.getString("data.notifications["+i+"].metadata.country");
            switch (Country){
                case "BR":
                    assertEquals(Country,"BR");
                    break;
                case "AR":
                    assertEquals(Country,"AR");
                    break;
                default:
                    fail("Warning , following countries not correct!");

            }
        }

    }


    /*
    TC:perPage value should correspond to the number of notifications retrieved
     */
    @Test
    public void PerPage(){
        given()
                .accept(ContentType.JSON).
        when()
                .get(url).
        then()
                .statusCode(200);

        Response response =get(baseURI);
        JsonPath jsonPath= response.jsonPath();

        int perPage=jsonPath.getInt("data.pageState.perPage");
        List<Integer> numberOfNotifications= jsonPath.getList("data.notifications");
        int numberOfNotif =numberOfNotifications.size();

        if (perPage==numberOfNotif){
            assertEquals(perPage,numberOfNotif);
        }else{
            fail("Warning, perpage value should not correspond to the number of notifications");
        }

    }
    /*
    TC:content of notifications should be a xml encoded on Base64
     */
    @Test
    public void checkEncoded(){
        given()
                .accept(ContentType.JSON).
        when()
                .get(url).
        then()
                .statusCode(200);

        Response response =get(baseURI);
        JsonPath jsonPath= response.jsonPath();

        List<Integer> numberOfNotifications= jsonPath.getList("data.notifications");
        int numberOfNotif =numberOfNotifications.size();

        for(int i=0 ;i < numberOfNotif ;i++ ){
            String contentOfNotifaction=jsonPath.getString("data.notifications["+i+"].content");

            if(APIUtils.isBase64(contentOfNotifaction)){
                assertTrue(APIUtils.isBase64(contentOfNotifaction));
            }else{
                fail("Warning , its not encoded Base64");

            }
        }
    }

    /**
     *     TC:notificationId should correspond to ID inside content xml document
     */
    @Test
    public void  checkNoficationId(){
        given().accept(ContentType.JSON)
                .and()
                .get(url)
                .then().assertThat()
                .statusCode(200);
        Response response =get(baseURI);
        JsonPath jsonPath= response.jsonPath();

        List<Integer> numberOfNotifications= jsonPath.getList("data.notifications");
        int numberOfNotif =numberOfNotifications.size();

        List<String> notificationIDs= new ArrayList<>();
        for (int i=0 ; i<numberOfNotif ; i++){
            String notificationID = jsonPath.getString("data.notifications["+i+"].notificationId");
            notificationIDs.add(notificationID);
        }

        List<String> IDs= new ArrayList<>();
        for(int i=0 ; i<numberOfNotif ; i++){
            String contents=jsonPath.getString("data.notifications["+i+"].content");
            String jsonString=APIUtils.decoderToJson(contents);
            JsonPath jsonPath1=JsonPath.from(jsonString);
            String ID=jsonPath1.getString("ApplicationResponse.ID");
            IDs.add(ID);
        }



        for (int i = 0; i < IDs.size(); i++) {
            if (notificationIDs.get(i).equals(IDs.get(i))){
                assertEquals(notificationIDs.get(i),IDs.get(i));
            }else{

                System.out.println(i+"-"+warnMessage(notificationIDs.get(i)));
            }
        }

    }

    /**
     * notificationId should be a valid GUID
     */
    @Test
    public void GUID(){
        given().accept(ContentType.JSON)
                .and()
                .get(url)
                .then().assertThat()
                .statusCode(200);
        Response response =get(baseURI);
        JsonPath jsonPath= response.jsonPath();

        List<Integer> numberOfNotifications= jsonPath.getList("data.notifications");
        int numberOfNotif =numberOfNotifications.size();

        SoftAssert softAssert= new SoftAssert();
        List<String> notificationIDs= new ArrayList<>();
        for (int i=0 ; i<numberOfNotif ; i++){
            String notificationID = jsonPath.getString("data.notifications["+i+"].notificationId");

            softAssert.assertTrue(APIUtils.isValidGUID(notificationID));
            if(!APIUtils.isValidGUID(notificationID)){
                System.out.println(notificationID);
                System.out.println("Warning NotificationId is not be valid GUID!");
            }
            notificationIDs.add(notificationID);
        }

        softAssert.assertAll();

    }

    /**
     * 200 notifications should have "Document Authorized" on StatusReason and "Document authorized successfully" on Text fields inside content xml document
     *
     */
    @Test
    public void check200Notifications(){
        given().accept(ContentType.JSON)
                .and()
                .get(url)
                .then().assertThat()
                .statusCode(200);
        Response response =get(baseURI);
        JsonPath jsonPath= response.jsonPath();

        List<Integer> numberOfNotifications= jsonPath.getList("data.notifications");
        int numberOfNotif =numberOfNotifications.size();

        String expectedStatusReason="Document Authorized";
        String expectedText="Document authorized successfully";

        for (int i = 0; i < numberOfNotif; i++) {
            String contents=jsonPath.getString("data.notifications["+i+"].content");
            String jsonString=APIUtils.decoderToJson(contents);
            JsonPath jsonPath1=JsonPath.from(jsonString);
            String Content=jsonPath1.getString("ApplicationResponse.DocumentResponse.Response.Status.StatusReasonCode.content");
            if(Content.equals("200")){
                assertEquals(jsonPath1.getString("ApplicationResponse.DocumentResponse.Response.Status.StatusReason"),expectedStatusReason);
                assertEquals(jsonPath1.getString("ApplicationResponse.DocumentResponse.Response.Status.Text"),expectedText);
            }else{
                System.out.println(i+".Notification's Content is equal 400");
            }

        }

    }

    /**
     *    400 notifications should have "Document Rejected" on StatusReason and "Document was rejected by tax authority" on Text fields inside content xml document
     */
    @Test
    public void check400Notifications(){
        given().accept(ContentType.JSON)
                .and()
                .get(url)
                .then().assertThat()
                .statusCode(200);
        Response response =get(baseURI);
        JsonPath jsonPath= response.jsonPath();

        List<Integer> numberOfNotifications= jsonPath.getList("data.notifications");
        int numberOfNotif =numberOfNotifications.size();

        String expectedStatusReason="Document Rejected";
        String expectedText="Document was rejected by tax authority";

        for (int i = 0; i < numberOfNotif; i++) {
            String contents=jsonPath.getString("data.notifications["+i+"].content");
            String jsonString=APIUtils.decoderToJson(contents);
            JsonPath jsonPath1=JsonPath.from(jsonString);
            String Content=jsonPath1.getString("ApplicationResponse.DocumentResponse.Response.Status.StatusReasonCode.content");
            if(Content.equals("400")){
                assertEquals(jsonPath1.getString("ApplicationResponse.DocumentResponse.Response.Status.StatusReason"),expectedStatusReason);
                assertEquals(jsonPath1.getString("ApplicationResponse.DocumentResponse.Response.Status.Text"),expectedText);
            }else{
                System.out.println(i+".Notification's Content is equal 200");
            }

        }

    }

    /**
     * Automation should display a warn in case of any rejected notification
     */
    @Test
    public String warnMessage(String s){
        return s + " FAILED!";

    }

}
