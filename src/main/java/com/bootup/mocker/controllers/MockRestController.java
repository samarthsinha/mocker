package com.bootup.mocker.controllers;

import com.bootup.mocker.dao.MockRequestResponse;
import com.bootup.mocker.repository.MockRequestResponseDbService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author b0095753 on 11/1/17.
 */
@RestController
@RequestMapping(value = {"/mock"})
public class MockRestController {

  @Autowired
  MockRequestResponseDbService mockRequestResponseDbService;

  @RequestMapping(value = {"/**"})
  public ResponseEntity<String> getResponse(HttpServletRequest request,HttpServletResponse response){
    String requestUri = request.getRequestURI();
    String soapAction = request.getHeader("SoapAction");
    String[] split = requestUri.split("/mock");
    if(split.length>1){
      requestUri = split[1];
    }
    String requestMethod = request.getMethod();
    MockRequestResponse mockRequestResponse = new MockRequestResponse();
    mockRequestResponse.setRequestUri(requestUri);
    if(StringUtils.hasText(soapAction)){
      mockRequestResponse.setSoap(true);
      mockRequestResponse.setSoapAction(soapAction);
    }
    mockRequestResponse.setRequestMethod(requestMethod.toUpperCase());
    ResponseEntity<String> responseEntity=null;
    MockRequestResponse mockPayload = mockRequestResponseDbService.findMock(mockRequestResponse);
    if(mockPayload!=null){
      Map<String, String> responseHeaders = mockPayload.getResponseHeaders();
      MultiValueMap<String,String> resHeaders = new LinkedMultiValueMap<>();
      resHeaders.setAll(responseHeaders);
      if(mockPayload.getDelayInSeconds()>0){
        long delayInSeconds = mockPayload.getDelayInSeconds();
        try {
          Thread.currentThread().sleep(delayInSeconds*1000);
        } catch (InterruptedException e) {
        }
      }
      responseEntity = new ResponseEntity<>(mockPayload.getResponseBody(),resHeaders,
          HttpStatus.valueOf(mockPayload.getHttpResponseCode()));
    }else{
      responseEntity = new ResponseEntity<String>("{\"status\":\"error\",\"message\":\"No such mapping found\"}",HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return responseEntity;
  }

  @GetMapping(value = {"/_mappings"})
  public ResponseEntity<List<MockRequestResponse>> getAllMappings(HttpServletRequest request,HttpServletResponse response){
    List<MockRequestResponse> allMockResponse = mockRequestResponseDbService.findAll();
    ResponseEntity<List<MockRequestResponse>> responseEntity = new ResponseEntity<List<MockRequestResponse>>(allMockResponse,HttpStatus.OK);
    return responseEntity;
  }

  @PostMapping(value = {"/save"})
  public String createMock(HttpServletRequest request,HttpServletResponse response, @RequestBody String body)
      throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    MockRequestResponse mockRequestResponse = objectMapper
        .readValue(body, MockRequestResponse.class);
    mockRequestResponseDbService.saveMockRequest(mockRequestResponse);
    return "{\"status\":\"success\",\"message\":\"saved new mock successfully\"}";
  }

}
