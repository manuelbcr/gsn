package ch.epfl.gsn.wrappers.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.ArgumentMatcher;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpCoreContext;

import ch.epfl.gsn.Main;
import ch.epfl.gsn.beans.AddressBean;
import ch.epfl.gsn.http.rest.RestRemoteWrapper;
import ch.epfl.gsn.storage.StorageManagerFactory;
import ch.epfl.gsn.utils.KeyValueImp;
import ch.epfl.gsn.wrappers.SensorMonitoringWrapper;
import ch.epfl.gsn.wrappers.backlog.BackLogMessageMultiplexer;


public class RemoteRestAPIWrapperTest {

    private RemoteRestAPIWrapper wrapper;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Setup current working directory
        String currentWorkingDir = System.getProperty("user.dir");
		if (!currentWorkingDir.endsWith("/gsn-core/")) {
			String newDirectory = currentWorkingDir + "/gsn-core/";
        	System.setProperty("user.dir", newDirectory);
		}
        
		Main.setDefaultGsnConf("/gsn_test.xml");
		Main.getInstance();
	}

    @Before
	public void setup() throws IOException{

        wrapper = new RemoteRestAPIWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("url", "url"));
        predicates.add( new KeyValueImp("client_id", "client_id"));
        predicates.add( new KeyValueImp("client_secret", "client_secret"));
        predicates.add( new KeyValueImp("vs_name", "vs_name"));
        
        AddressBean ab = new AddressBean("restRemote",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("restRemote");

        HttpClient httpClientMock = Mockito.mock(HttpClient.class);
        
        // response for oauth2/token
        StatusLine statusLineToken = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        HttpResponse responseToken = new BasicHttpResponse(statusLineToken);
        String accessTokenValue = "123456789";
        HttpEntity entityToken = new StringEntity("{\"access_token\":\"" + accessTokenValue + "\"}");
        responseToken.setEntity(entityToken);

        Mockito.when(httpClientMock.execute(Mockito.argThat(new HttpRequestWithSpecificURLMatcher("url/oauth2/token")))).thenReturn(responseToken);

        // response for /api/sensors/vs_name
        HttpResponse responseSensor = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
        String contentSensor = "{\"properties\":{\"fields\":[{\"name\":\"timestamp\",\"type\":\"BIGINT\"},{\"name\":\"field1\",\"type\":\"INTEGER\"},{\"name\":\"field2\",\"type\":\"INTEGER\"}]}}";
        HttpEntity entitySensor = new StringEntity(contentSensor);
        responseSensor.setEntity(entitySensor);

        Mockito.when(httpClientMock.execute(Mockito.argThat(new HttpRequestWithSpecificURLMatcher("url/api/sensors/vs_name")))).thenReturn(responseSensor);

        // response for /api/sensors/vs_name?/data?from=
        HttpResponse responseSensorData = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
        String contentSensorData = "{\"properties\":{\"fields\":[{\"name\":\"timestamp\",\"type\":\"BIGINT\"},{\"name\":\"field1\",\"type\":\"INTEGER\"},{\"name\":\"field2\",\"type\":\"INTEGER\"}],\"values\":[[123456789, 42, 56],[123456790, 38, 72],[123456791, 65, 89]]}}";
        HttpEntity entitySensorData = new StringEntity(contentSensorData);
        responseSensorData.setEntity(entitySensorData);

        Mockito.when(httpClientMock.execute(Mockito.argThat(new HttpRequestWithSpecificURLMatcher("url/api/sensors/vs_name/data?from=")))).thenReturn(responseSensorData);
       
        wrapper.setClient(httpClientMock);
        wrapper.setActiveAddressBean(ab);

        assertTrue(wrapper.initialize());
        assertEquals("Remote REST API Wrapper", wrapper.getWrapperName());
    }


    @Test
    public void testInitializeMissingUrl(){

        RemoteRestAPIWrapper wrapper = new RemoteRestAPIWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
        predicates.add( new KeyValueImp("client_id", "client_id"));
        predicates.add( new KeyValueImp("client_secret", "client_secret"));
        predicates.add( new KeyValueImp("vs_name", "vs_name"));
		
        AddressBean ab = new AddressBean("restRemote",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("restRemote");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testInitializeMissingClientId(){

        RemoteRestAPIWrapper wrapper = new RemoteRestAPIWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("url", "url"));
        predicates.add( new KeyValueImp("client_secret", "client_secret"));
        predicates.add( new KeyValueImp("vs_name", "vs_name"));
		
        AddressBean ab = new AddressBean("restRemote",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("restRemote");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testInitializeMissingClientSecret(){

        RemoteRestAPIWrapper wrapper = new RemoteRestAPIWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("url", "url"));
        predicates.add( new KeyValueImp("client_id", "client_id"));
        predicates.add( new KeyValueImp("vs_name", "vs_name"));
		
        AddressBean ab = new AddressBean("restRemote",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("restRemote");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testInitializeMissingVsName(){

        RemoteRestAPIWrapper wrapper = new RemoteRestAPIWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("url", "url"));
        predicates.add( new KeyValueImp("client_id", "client_id"));
        predicates.add( new KeyValueImp("client_secret", "client_secret"));
		
        AddressBean ab = new AddressBean("restRemote",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("restRemote");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }

    @Test
    public void testInitializeWrongStartTime(){

        RemoteRestAPIWrapper wrapper = new RemoteRestAPIWrapper();

        ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp("url", "url"));
        predicates.add( new KeyValueImp("client_id", "client_id"));
        predicates.add( new KeyValueImp("client_secret", "client_secret"));
        predicates.add( new KeyValueImp("vs_name", "vs_name"));
        predicates.add( new KeyValueImp("starting_time", "starting_time"));
		
        AddressBean ab = new AddressBean("restRemote",predicates.toArray(new KeyValueImp[] {}));
        ab.setVirtualSensorName("restRemote");

        wrapper.setActiveAddressBean(ab);

        assertFalse(wrapper.initialize());

    }


    @Test
    public void testRun(){

        wrapper.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.stop();
        wrapper.dispose();


    }

    @Test
    public void testRunError() throws IOException, UnsupportedEncodingException{

        HttpClient httpClientMock = Mockito.mock(HttpClient.class);

        StatusLine statusLineToken = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        HttpResponse responseToken = new BasicHttpResponse(statusLineToken);
        String accessTokenValue = "123456789";
        HttpEntity entityToken = new StringEntity("{\"access_token\":\"" + accessTokenValue + "\"}");
        responseToken.setEntity(entityToken);

        Mockito.when(httpClientMock.execute(Mockito.argThat(new HttpRequestWithSpecificURLMatcher("url/oauth2/token")))).thenReturn(responseToken);

        // response for /api/sensors/vs_name
        HttpResponse responseSensor = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 401, "OK"));
        String contentSensor = "{\"properties\":{\"fields\":[{\"name\":\"timestamp\",\"type\":\"BIGINT\"},{\"name\":\"field1\",\"type\":\"INTEGER\"},{\"name\":\"field2\",\"type\":\"INTEGER\"}]}}";
        HttpEntity entitySensor = new StringEntity(contentSensor);
        responseSensor.setEntity(entitySensor);

        Mockito.when(httpClientMock.execute(Mockito.argThat(new HttpRequestWithSpecificURLMatcher("url/api/sensors/vs_name")))).thenReturn(responseSensor);

        // response for /api/sensors/vs_name?/data?from=
        HttpResponse responseSensorData = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 401, "OK"));
        String contentSensorData = "{\"properties\":{\"fields\":[{\"name\":\"timestamp\",\"type\":\"BIGINT\"},{\"name\":\"field1\",\"type\":\"INTEGER\"},{\"name\":\"field2\",\"type\":\"INTEGER\"}],\"values\":[[123456789, 42, 56],[123456790, 38, 72],[123456791, 65, 89]]}}";
        HttpEntity entitySensorData = new StringEntity(contentSensorData);
        responseSensorData.setEntity(entitySensorData);

        Mockito.when(httpClientMock.execute(Mockito.argThat(new HttpRequestWithSpecificURLMatcher("url/api/sensors/vs_name/data?from=")))).thenReturn(responseSensorData);

        wrapper.setClient(httpClientMock);

        wrapper.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.stop();
        wrapper.dispose();

    }



    public class HttpRequestWithSpecificURLMatcher implements ArgumentMatcher<HttpRequestBase> {
        private final String expectedURL;
    
        public HttpRequestWithSpecificURLMatcher(String expectedURL) {
            this.expectedURL = expectedURL;
        }
    
        @Override
        public boolean matches(HttpRequestBase argument) {
            if (argument == null || argument.getURI() == null) {
                return false;
            }

            if(argument.getURI().toString().contains("=")){
                int index1 = argument.getURI().toString().indexOf("=");
                int index2 = expectedURL.indexOf("=");

                String substr1 = argument.getURI().toString().substring(0, index1);
                String substr2 = expectedURL.substring(0, index2);

                // Compare the substrings
                if (substr1.equals(substr2)) {
                    return true;
                } else {
                    return false;
                }
            }
    
    
            return argument.getURI().toString().equals(expectedURL);
        }
    }

   
    
}
