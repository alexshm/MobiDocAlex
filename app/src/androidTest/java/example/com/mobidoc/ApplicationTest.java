package example.com.mobidoc;

import android.app.Application;
import android.test.ApplicationTestCase;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private OpenMrsApi openMrsApi;
    String baseUrl;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        baseUrl = "http://" + "mobidoc.ise.bgu.ac.il" + ":8081/openmrs-standalone/ws/rest/v1/";
        openMrsApi = new OpenMrsApi(baseUrl);
    }

    public void testLogIn() throws Exception {
        assertEquals(false,openMrsApi.logIn("israel","badPass"));
        assertEquals(true,openMrsApi.logIn("israel","Israel123"));
    }

    public void testEnterMeasure() throws Exception {
        openMrsApi.logIn("israel","Israel123");
        String concept =  "5085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        String dateTime = "2010-03-23T00:00:00.000+0200";
        assertTrue("problem"!=openMrsApi.enterMeasure("76",dateTime,"systolic"));
    }

    public void testGetMeasures() throws Exception {
        openMrsApi.logIn("israel","Israel123");
        String[] array = openMrsApi.getObs();
        assertTrue(0 < array.length);
        assertTrue(array[0] != null);
    }

    public void testGetPersonUUID() throws Exception {
        openMrsApi.logIn("israel","Israel123");
        assertTrue("problem" != openMrsApi.getPersonUUID());
    }
    public void testGetPatientUUID() throws Exception {
        openMrsApi.logIn("israel","Israel123");
        assertTrue("problem" != openMrsApi.getPatintUuid());
    }
    public void testGetUserUUID() throws Exception {
        openMrsApi.logIn("israel","Israel123");
        assertTrue("problem" != openMrsApi.getUserUuid());
    }
    public void testGetConceptUuid() throws Exception {
        openMrsApi.logIn("israel","Israel123");
        assertTrue("problem" != openMrsApi.getConceptUuid("systolic"));
    }

    public ApplicationTest() {
        super(Application.class);
    }

}