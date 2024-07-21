package com.fanap.fanrp.pat.patemailserver.async;

import com.fanap.fanrp.pat.patemailserver.enums.MessageType;
import com.fanap.fanrp.pat.patemailserver.utils.JsonUtil;
import com.fanap.fanrp.pat.patemailserver.vo.MessageWrapperVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RestCallerImpl implements RestCaller{

    private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";
    private static final String AUTH_TYPE = "Bearer";
    private static final String AUTH_FORMAT = "%s %s";
    private static final String LOGIN_FAIL_MESSAGE = "Login Fail";
    private static final String DATA_KEY = "data";
    private String TOKEN;
    private String ID_TOKEN;
    private final String ENGINE_NAME;
    private static String ASYNC_URL;

    private final HttpClient httpClient;
    private final PostResponseHandler postResponseHandler;

    static {
        ASYNC_URL = "http://172.16.110.235:8003/srv";
    }
    protected Consumer<HttpRequestBase> requestDefaultHeader = request -> {
        request.addHeader(HttpHeaders.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        makeAuthorizationValue().ifPresent(value -> request.addHeader(HttpHeaders.AUTHORIZATION, value));
    };

    public RestCallerImpl(String engineName,String token) {
        this.ENGINE_NAME = engineName;
        this.httpClient = HttpClientBuilder.create().setMaxConnPerRoute(5).setMaxConnTotal(10).build();;
        this.postResponseHandler = new PostResponseHandler();
        this.TOKEN = token;
    }

    public RestCallerImpl(String engineName) {
        this.ENGINE_NAME = engineName;
        this.httpClient = HttpClientBuilder.create().setMaxConnPerRoute(5).setMaxConnTotal(10).build();;
        this.postResponseHandler = new PostResponseHandler();
        this.TOKEN = null;
    }

    public  Response sendToAsync(Request request, byte asyncMessageType) throws Exception {
        fillMeta(request);
        String d = JsonUtil.write(request);
        System.out.println(" >>: "+d);
        MessageWrapperVO vo = toMessageWrapperVO(d, asyncMessageType);
        Map<String, Object> parameters = makeParameters(vo);
        PostResponseHandler postResponseHandler = new PostResponseHandler();
        HttpPost postRequest = createPostRequest(ASYNC_URL, parameters);
        String execute = httpClient.execute(postRequest, postResponseHandler);
        postRequest.releaseConnection();
        return new Response(execute);
    }

    public String callPost(String url, Map<String, Object> parameters) throws Exception {
        return null;
    }

    private Map<String, Object> makeParameters(MessageWrapperVO vo) throws JsonProcessingException {
        HashMap<String, Object> params = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        params.put(DATA_KEY, objectMapper.writeValueAsString(vo));
        return params;
    }

    private MessageWrapperVO toMessageWrapperVO(String msg, byte asyncMessageType) throws JsonProcessingException {
        PeerContent peerContent = new PeerContent();
        peerContent.setPeerName("tfa.ow");
        peerContent.setContent(msg);
        MessageWrapperVO message = new MessageWrapperVO();
        message.setType(asyncMessageType);
        ObjectMapper objectMapper = new ObjectMapper();
        message.setContent(JsonUtil.write(peerContent));
        message.setTrackerId(generateTrackerId());
        return message;
    }

    private Optional<String> makeAuthorizationValue() {
        String value;
        if (this.TOKEN != null) {
            value = String.format(AUTH_FORMAT, AUTH_TYPE, this.TOKEN);
            System.out.println("Make authorization header value with engine token");
        } else if (this.ID_TOKEN != null) {
            value = String.format(AUTH_FORMAT, AUTH_TYPE, this.ID_TOKEN);
            System.out.println("Make authorization header value with id token");
        } else {
            value = null;
        }
        return Optional.ofNullable(value);
    }

    private HttpPost createPostRequest(String url, Map<String, Object> parameters) {
        HttpPost post = new HttpPost(url);
        post.setEntity(createUrlEncodedFormEntity(createUrlParameters(parameters)));
        requestDefaultHeader.accept(post);
        return post;
    }

    private List<NameValuePair> createUrlParameters(Map<String, Object> parameters) {
        return parameters.entrySet().stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue().toString()))
                .collect(Collectors.toList());
    }

    private UrlEncodedFormEntity createUrlEncodedFormEntity(List<NameValuePair> urlParameters) {
        return new UrlEncodedFormEntity(urlParameters, StandardCharsets.UTF_8);
    }

    private void fillMeta(Request request) {
        RequestHeader header = request.getRequestHeader();
        if (header == null) {
            header = new RequestHeader();
        }
        if (request.getMessageType().equals(MessageType.INIT_PROCESS)) {
            request.getProcessData().iterator().next().setProcessTrackerId(UUID.randomUUID().toString());
        }
        header.setPeerName(ENGINE_NAME);
        if (this.TOKEN != null) {
            header.setToken(this.TOKEN);
        }
        if (this.ID_TOKEN != null) {
            header.setId_token(this.ID_TOKEN);
        }
        /*if (!this.useEngineToken.get()) {
            header.setCsrf_token(this.csrf.get());
        }*/
        header.setRequesterPostId(25l);

        request.setRequestHeader(header);
        request.setEngineName(this.ENGINE_NAME);
        request.setTrackerId(String.valueOf(generateTrackerId()));
    }

    private long generateTrackerId() {
        long LOWER_RANGE = 100000;
        long UPPER_RANGE = 10000000;
        return LOWER_RANGE + (long) (new Random().nextDouble() * (UPPER_RANGE - LOWER_RANGE));
    }
}
