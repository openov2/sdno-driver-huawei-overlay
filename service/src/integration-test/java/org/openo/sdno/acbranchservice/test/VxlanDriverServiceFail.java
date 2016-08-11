package org.openo.sdno.acbranchservice.test;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openo.baseservice.remoteservice.exception.ServiceException;
import org.openo.sdno.acbranchservice.test.mocoserver.VxlanDriverServiceFailServer;
import org.openo.sdno.testframework.checker.IChecker;
import org.openo.sdno.testframework.checker.RegularExpChecker;
import org.openo.sdno.testframework.http.model.HttpModelUtils;
import org.openo.sdno.testframework.http.model.HttpResponse;
import org.openo.sdno.testframework.http.model.HttpRquestResponse;
import org.openo.sdno.testframework.testmanager.TestManager;
import org.openo.sdno.testframework.util.file.FileUtils;

import com.google.gson.Gson;

import net.sf.json.JSONObject;

public class VxlanDriverServiceFail extends TestManager{
	
private VxlanDriverServiceFailServer vxlanServer = new VxlanDriverServiceFailServer();
	
	@Before
	public void setup() throws ServiceException {
		vxlanServer.start();
	}

	@After
	public void tearDown() {
		vxlanServer.stop();
	}

	@Test
	public void test() throws ServiceException {
		
		File createFile = new File("src/integration-test/resources/ACBranchDriver/createFail.json");
	    HttpRquestResponse createHttpObject =
	            HttpModelUtils.praseHttpRquestResponse(FileUtils.readFromJson(createFile));
	    HttpResponse createResponse = execTestCase(createFile,
	            new RegularExpChecker2(createHttpObject.getResponse()));
	    String response = createResponse.getData();
	    
	   createFile = new File("src/integration-test/resources/ACBranchDriver/deleteFail.json");
	    createHttpObject =
	            HttpModelUtils.praseHttpRquestResponse(FileUtils.readFromJson(createFile));
	    createResponse = execTestCase(createFile,
	            new RegularExpChecker2(createHttpObject.getResponse()));
	    response = createResponse.getData();
	    
	    createFile = new File("src/integration-test/resources/ACBranchDriver/queryFail.json");
	    createHttpObject =
	            HttpModelUtils.praseHttpRquestResponse(FileUtils.readFromJson(createFile));
	    createResponse = execTestCase(createFile,
	            new RegularExpChecker2(createHttpObject.getResponse()));
	    response = createResponse.getData();
	}
	
	//created this class for a workaround as expectedResponse.getData() produce result "null"
	public class RegularExpChecker2 extends RegularExpChecker {

	    private HttpResponse expectedResponse;

	    public RegularExpChecker2(HttpResponse response) {
	    	super(response);
	        expectedResponse = response;

	    }

		// Regular expression checker
	    @Override
	    public boolean check(HttpResponse response) {
	        if(response.getStatus() != expectedResponse.getStatus()) {
	            return false;
	        }

	        // If expected response is null -- no need to match anything, only check status
	        if("null".equals(expectedResponse.getData())) {
	            return true;
	        }

	        // Something is expected but nothing came, some problem, test case failed
	        if(null == response.getData()) {
	            return false;
	        }

	        return new RegularExpChecker(response).
	        		check(JSONObject.fromObject(expectedResponse.getData()), JSONObject.fromObject(response.getData()));
	    }
	}
}
