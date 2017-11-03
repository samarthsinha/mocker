package com.bootup.mocker;

import com.bootup.mocker.dao.MockRequestResponse;
import com.bootup.mocker.repository.MockRequestResponseDbService;
import com.bootup.mocker.utils.EncryptionUtils;
import com.bootup.mocker.utils.EncryptionUtils.HashingAlgorithm;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MockerApplicationTests {


	@Autowired
	MockRequestResponseDbService mockRequestResponseDbService;

	@Before
	public void init(){
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void contextLoads() throws NoSuchAlgorithmException {
		MockRequestResponse mockRequestResponse = new MockRequestResponse();
		mockRequestResponse.setRequestUri("/test/god/mode");
		mockRequestResponse.setRequestMethod("GET");
		String encodedId = mockRequestResponseDbService.getEncodedId(mockRequestResponse);
		System.out.println(encodedId);
		Assert.assertEquals(EncryptionUtils.getHashedString(HashingAlgorithm.SHA_256,"/test/god/mode_GET"),encodedId);
		mockRequestResponse = new MockRequestResponse();
		mockRequestResponse.setSoap(Boolean.TRUE);
		mockRequestResponse.setRequestUri("/123123/1231231/kjdas?wsdl=true");
		mockRequestResponse.setSoapAction("GetURL:sds");
		encodedId = mockRequestResponseDbService.getEncodedId(mockRequestResponse);
		System.out.println(encodedId);
		Assert.assertEquals(EncryptionUtils.getHashedString(HashingAlgorithm.SHA_256,"/123123/1231231/kjdas?wsdl=true_GetURL:sds"),encodedId);
	}

	@Test
	public void testMongoSaveRequest() throws NoSuchAlgorithmException {
		MockRequestResponse mockRequestResponse = new MockRequestResponse();
		mockRequestResponse.setRequestUri("/test/god/mode");
		mockRequestResponse.setRequestMethod("GET");
		mockRequestResponse.setResponseContentType("application/json");
		mockRequestResponse.setContentEncoding("UTF-8");
		mockRequestResponse.setHttpResponseCode(HttpStatus.OK.value());
		mockRequestResponse.setResponseBody("{\"store\":\"adidas\"}");
		mockRequestResponse.setResponseHeaders(new HashMap<String,String>(){{put("auth","tooakd)909");}});
		mockRequestResponseDbService.saveMockRequest(mockRequestResponse);

		String id = mockRequestResponseDbService.getEncodedId(mockRequestResponse);
		MockRequestResponse mockRequestResponse1 = mockRequestResponseDbService.findMockPayload(id);
		Assert.assertNotNull("Failed to retrieve object from mongo!",mockRequestResponse1);
		Assert.assertEquals(mockRequestResponse.getId(),mockRequestResponse1.getId());
		Assert.assertEquals(mockRequestResponse.getContentEncoding(),mockRequestResponse1.getContentEncoding());
		Assert.assertEquals(mockRequestResponse.getHttpResponseCode(),mockRequestResponse1.getHttpResponseCode());
		Assert.assertEquals(mockRequestResponse.getResponseContentType(),mockRequestResponse1.getResponseContentType());
		Assert.assertEquals(mockRequestResponse.getRequestUri(),mockRequestResponse1.getRequestUri());
		Assert.assertEquals(mockRequestResponse.getRequestMethod(),mockRequestResponse1.getRequestMethod());
		Assert.assertEquals(mockRequestResponse.getResponseBody(),mockRequestResponse1.getResponseBody());
		Assert.assertEquals(mockRequestResponse.getResponseHeaders(),mockRequestResponse1.getResponseHeaders());
		Assert.assertEquals(mockRequestResponse.getCreatedTime(),mockRequestResponse1.getCreatedTime());
		Assert.assertEquals(mockRequestResponse.getUpdatedTime(),mockRequestResponse1.getUpdatedTime());

	}

//	@After
//	public void flushTestDbs(){
//		mockRequestResponseDbService.cleanCollection();
//	}

}
