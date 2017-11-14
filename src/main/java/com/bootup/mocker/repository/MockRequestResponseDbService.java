package com.bootup.mocker.repository;

import com.bootup.mocker.dao.MockRequestResponse;
import com.bootup.mocker.utils.EncryptionUtils;
import com.bootup.mocker.utils.EncryptionUtils.HashingAlgorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author b0095753 on 11/2/17.
 */
@Service
public class MockRequestResponseDbService {

  private static final Logger LOGGER = LogManager.getLogger(MockRequestResponseDbService.class);

  private final MongoTemplate mongoTemplate;

  private final RedisTemplate<String, String> redisTemplate;

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final String KEY_PREF = "MOCK_ID_";


  @Autowired
  public MockRequestResponseDbService(
      MongoTemplate mongoTemplate,RedisTemplate<String, String> redisTemplate) {
    Assert.notNull(mongoTemplate,"Mongo Template is required");
    this.redisTemplate = redisTemplate;
    this.mongoTemplate = mongoTemplate;
  }


  /**
   *
   * @param mockRequestResponse
   */
  public void saveMockRequest(MockRequestResponse mockRequestResponse){
    Assert.notNull(mockRequestResponse,"Expected not null object.");
    long creationTime = System.currentTimeMillis();
    if(StringUtils.isEmpty(mockRequestResponse.getId())){
      mockRequestResponse.setCreatedTime(creationTime);
      try {
        mockRequestResponse.setId(getEncodedId(mockRequestResponse));
        redisTemplate.delete(KEY_PREF+mockRequestResponse.getId());
      } catch (NoSuchAlgorithmException e) {
        return;
      }
    }
    mockRequestResponse.setUpdatedTime(creationTime);
    mongoTemplate.save(mockRequestResponse);
  }

  /**
   *
   * @param mockRequestResponse
   * @return
   */
  public MockRequestResponse findMock(MockRequestResponse mockRequestResponse){
    try {
      String id = getEncodedId(mockRequestResponse);
      return findMockPayload(id);
    } catch (NoSuchAlgorithmException e) {
    }
    return null;
  }

  /**
   *
   * @param id
   * @return
   */
  public MockRequestResponse findMockPayload(String id){
    String mockObjStr = redisTemplate.opsForValue().get(KEY_PREF+id);
    if(!StringUtils.isEmpty(mockObjStr)){
      try {
        return objectMapper.readValue(mockObjStr,MockRequestResponse.class);
      } catch (IOException e) {
      }
    }
    MockRequestResponse byId = mongoTemplate.findById(id, MockRequestResponse.class);
    if(byId!=null){
      try {
        String s = objectMapper.writeValueAsString(byId);
        redisTemplate.opsForValue().set(KEY_PREF+id,s, 60*60, TimeUnit.SECONDS);
      } catch (JsonProcessingException e) {
      }
    }
    return byId;
  }

  public List<MockRequestResponse> findAll(){
    List<MockRequestResponse> all = mongoTemplate.findAll(MockRequestResponse.class);
    return all;
  }


  public void cleanCollection(){
    mongoTemplate.dropCollection(MockRequestResponse.class);
  }


  /**
   *
   * @param mockRequestResponse
   * @return
   */
  public String getEncodedId(MockRequestResponse mockRequestResponse)
      throws NoSuchAlgorithmException {
    Assert.notNull(mockRequestResponse,"Expected not null object.");
    StringBuilder idBuilder = new StringBuilder();
    idBuilder.append(mockRequestResponse.getRequestUri());
    idBuilder.append("_");
    if(mockRequestResponse.getSoap()){
      idBuilder.append(mockRequestResponse.getSoapAction());
    }else {
      idBuilder.append(mockRequestResponse.getRequestMethod());
    }
    return EncryptionUtils.getHashedString(HashingAlgorithm.SHA_256,idBuilder.toString());
  }

}
