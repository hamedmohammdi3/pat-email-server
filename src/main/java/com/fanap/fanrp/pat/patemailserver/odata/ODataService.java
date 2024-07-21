package com.fanap.fanrp.pat.patemailserver.odata;


import com.fanap.fanrp.pat.patemailserver.exception.InvalidConfigurationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.olingo.odata2.api.commons.HttpHeaders;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataDeltaFeed;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Bastam on 11/26/2017.
 */
public class ODataService {

//    private static final FanRPLogger logger = LoggerService.getLoggerInstance("ODataService", ODataService.class);

    private static final String HTTP_METHOD_PUT = "PUT";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_DELETE = "DELETE";

    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_XML = "application/xml";
    private static final String usedFormat = APPLICATION_JSON;

    private static final String METADATA = "$metadata";
    private static final String SEPARATOR = "/";
    private static final boolean PRINT_RAW_CONTENT = true;

    private static boolean needToReloadMetaData = true;
    private static boolean needToReloadEntitySet = true;

    private static ODataService instance;
    private static String serviceUrl;
    private static String engName;

    private static Edm EDM;
    private static String TOKEN;


    static {
        try {
//            processService = DaivaServiceFactory.getProcessService();
            serviceUrl = "http://172.16.110.47:8080/report50/odata2.svc";
            engName = ODataService.getEngineName();
        } catch (Exception e) {
//            logger.error("problem in reading odata servcie url", e);
            e.printStackTrace();
        }
    }

    public ODataService(String token) throws IOException, ODataException {
        if (EDM == null) {
            EDM = this.readEdm();
        }
        TOKEN = token;
    }

    public synchronized static ODataService getInstance(String token) throws IOException, ODataException {
        if (instance == null) {
            instance = new ODataService(token);
        }
        TOKEN = token;
        return instance;
    }

    private static String getEngineName() throws Exception {
        String engineName = null;
        try {
            String enginesName = "tfa.ow";
            String[] names = enginesName.split(",");
            if (names != null && names.length > 0) {
                engineName = names[0];
                if (engineName == null || engineName.trim().isEmpty()) {
                    throw new Exception("can not find any engine name.");
                } else {
                    return engineName.toLowerCase();
                }
            } else {
                throw new Exception("can not find any engine name.");
            }
        } catch (IOException | InvalidConfigurationException e) {
            throw e;
        }
    }

    private static void print(String content) {
        System.out.println(content);
    }

    static String prettyPrint(ODataEntry createdEntry) {
        return prettyPrint(createdEntry.getProperties(), 0);
    }

    private static String prettyPrint(Map<String, Object> properties, int level) {
        StringBuilder b = new StringBuilder();
        Set<Map.Entry<String, Object>> entries = properties.entrySet();

        for (Map.Entry<String, Object> entry : entries) {
            intend(b, level);
            b.append(entry.getKey()).append(": ");
            Object value = entry.getValue();
            if(value instanceof Map) {
                value = prettyPrint((Map<String, Object>)value, level+1);
                b.append(value).append("\n");
            } else if(value instanceof Calendar) {
                Calendar cal = (Calendar) value;
                value = SimpleDateFormat.getInstance().format(cal.getTime());
                b.append(value).append("\n");
            } else if(value instanceof ODataDeltaFeed) {
                ODataDeltaFeed feed = (ODataDeltaFeed) value;
                List<ODataEntry> inlineEntries =  feed.getEntries();
                b.append("{");
                for (ODataEntry oDataEntry : inlineEntries) {
                    value = prettyPrint((Map<String, Object>)oDataEntry.getProperties(), level+1);
                    b.append("\n[\n").append(value).append("\n],");
                }
                b.deleteCharAt(b.length()-1);
                intend(b, level);
                b.append("}\n");
            } else {
                b.append(value).append("\n");
            }
        }
        // remove last line break
        b.deleteCharAt(b.length()-1);
        return b.toString();
    }

    private static void intend(StringBuilder builder, int intendLevel) {
        for (int i = 0; i < intendLevel; i++) {
            builder.append("  ");
        }
    }

    public Edm readEdm() throws IOException, EntityProviderException {
        /*UserVo userVo = null;
        try {
            userVo = processService.getCurrentUserDataWithoutRoleAndPost();
        } catch (Exception e) {
            logger.error("An error occurred while getting current user data to get token from it.", e);
        }*/
        InputStream content = execute(serviceUrl + SEPARATOR + METADATA + "/?$serverName=" + engName, APPLICATION_XML, HTTP_METHOD_GET);
        return EntityProvider.readMetadata(content, false);

    }

    public void updateEdm() throws IOException, ODataException, InvalidConfigurationException {
        serviceUrl = "http://172.16.110.47:8080/report50/odata2.svc";
        EDM = this.readEdm();
    }

    public ODataFeed readFeed(String entitySetName,boolean isOther )
            throws IOException, ODataException {
        String absolutUri = createUri(serviceUrl, entitySetName, null,null,null,engName);

        InputStream content = execute(absolutUri, usedFormat, HTTP_METHOD_GET);
        return entityProviderReadFeed(entitySetName, content);
    }

    public ODataFeed readFeed(String entitySetName,boolean isOther, String oDataSource)
            throws IOException, ODataException {
        String absolutUri = createUri(serviceUrl, entitySetName, null,null,null,oDataSource);

        InputStream content = execute(absolutUri, usedFormat, HTTP_METHOD_GET);
        return entityProviderReadFeed(entitySetName, content);
    }

    public ODataFeed readFeed(String entitySetName)
            throws IOException, ODataException {
        return readFeed(entitySetName,false);
    }

    public ODataFeed readFeed(String entitySetName, String oDataSource)
            throws IOException, ODataException {
        return readFeed(entitySetName,false, oDataSource);
    }

    public ODataFeed readFeedByFilter(String entitySetName , String filter , boolean isOther)
            throws IOException, ODataException {
        return readFeedByFilter(entitySetName, filter, "", isOther);
    }

    public ODataFeed readFeedByFilter(String entitySetName , String filter , String option, boolean isOther)
            throws IOException, ODataException {
        String absolutUri = createUri(serviceUrl, entitySetName, null , null , filter,engName, option);

        InputStream content = execute(absolutUri, usedFormat, HTTP_METHOD_GET);

        return entityProviderReadFeed(entitySetName, content);
    }

    public ODataFeed readFeedByFilter(String entitySetName , String filter , String option, boolean isOther, String oDataSource)
            throws IOException, ODataException {
        String absolutUri = createUri(serviceUrl, entitySetName, null, null, filter, oDataSource, option);

        InputStream content = execute(absolutUri, usedFormat, HTTP_METHOD_GET);
        return entityProviderReadFeed(entitySetName, content);
    }

    public ODataFeed readFeedByFilter(String entitySetName , String filter)
            throws IOException, ODataException {
        return readFeedByFilter(entitySetName,filter,false);
    }

    public ODataFeed readFeedByFilter(String entitySetName , String filter, String option)
            throws IOException, ODataException {
        return readFeedByFilter(entitySetName,filter, option, false);
    }

    public ODataFeed readFeedByFilter(String entitySetName , String filter, String option, String oDataSource)
            throws IOException, ODataException {
        return readFeedByFilter(entitySetName,filter, option, false, oDataSource);
    }

    public ODataEntry readFirstFeedByFilter(String entitySetName, String filter, boolean isOther)
            throws IOException, ODataException {
        return readFirstFeedByFilter(entitySetName, filter, isOther, ODataSource.APP);
    }

    public ODataEntry readFirstFeedByFilter(String entitySetName, String filter, boolean isOther, ODataSource oDataSource)
            throws IOException, ODataException {
        return readFirstFeedByFilter(entitySetName, filter, isOther, engName + oDataSource.getValue());
    }

    public ODataEntry readFirstFeedByFilter(String entitySetName, String filter, boolean isOther, String oDataSource)
            throws IOException, ODataException {
        String absoluteUri = createUri(serviceUrl, entitySetName, null , null , filter, oDataSource);

        InputStream content = execute(absoluteUri, usedFormat, HTTP_METHOD_GET);
        List<ODataEntry> entries = entityProviderReadFeed(entitySetName, content).getEntries();

        ODataEntry firstFeed = null;
        if (entries != null && !entries.isEmpty()) {
            firstFeed = entries.get(0);
        }

        return firstFeed;
    }

    public ODataEntry readFirstFeedByFilter(String entitySetName, String filter)
            throws IOException, ODataException {
        return  readFirstFeedByFilter(entitySetName,filter,false);
    }

    public ODataEntry readEntry(String entitySetName, String keyValue, boolean isOther)
            throws IOException, ODataException {
        return readEntry(entitySetName, keyValue, null , null,isOther);
    }

    public ODataEntry readEntry(String entitySetName, String keyValue)
            throws IOException, ODataException {
        return readEntry(entitySetName,keyValue,false);
    }

    @Deprecated
    public ODataEntry readEntryByProperty(String entitySetName,String key , String keyValue)
            throws IOException, ODataException {
        return readEntryByProperty(entitySetName,key,keyValue,false);
    }

    @Deprecated
    public ODataEntry readEntryByProperty(String entitySetName,String key , String keyValue,boolean isOther)
            throws IOException, ODataException {
        return this.readFirstFeedByFilter(entitySetName, key+" eq "+keyValue,isOther);
    }

    public ODataEntry readEntryByObjectProperty(String entitySetName,String key , Object keyValue)
            throws IOException, ODataException {
        return readEntryByObjectProperty(entitySetName, key, keyValue,false);
    }

    public ODataEntry readEntryByObjectProperty(String entitySetName,String key , Object keyValue, String oDataSource)
            throws IOException, ODataException {
        return readEntryByObjectProperty(entitySetName, key, keyValue,false, oDataSource);
    }

    public ODataEntry readEntryByObjectProperty(String entitySetName,String key , Object keyValue, ODataSource oDataSource)
            throws IOException, ODataException {
        return readEntryByObjectProperty(entitySetName,key,keyValue,false, oDataSource);
    }

    public ODataEntry readEntryByObjectProperty(String entitySetName,String key , Object keyValue,boolean isOther)
            throws IOException, ODataException {

        return readEntryByObjectProperty(entitySetName, key, keyValue, isOther, ODataSource.APP);
    }

    public ODataEntry readEntryByObjectProperty(String entitySetName,String key , Object keyValue,boolean isOther, String oDataSource)
            throws IOException, ODataException {
        if (keyValue instanceof Boolean){
            if ((Boolean) keyValue) {
                keyValue = "'1'";
            } else {
                keyValue = "'0'";
            }
        } else if (keyValue instanceof String){
            if (!((String) keyValue).trim().startsWith("'") && !((String) keyValue).trim().endsWith("'")) {
                keyValue = "'" + keyValue + "'";
            }
        } else {
            keyValue = keyValue.toString();
        }
        return this.readFirstFeedByFilter(entitySetName, key+" eq "+keyValue,isOther, oDataSource);
    }

    public ODataEntry readEntryByObjectProperty(String entitySetName,String key , Object keyValue,boolean isOther, ODataSource oDataSource)
            throws IOException, ODataException {
        if (keyValue instanceof Boolean){
            if ((Boolean) keyValue) {
                keyValue = "'1'";
            } else {
                keyValue = "'0'";
            }
        } else if (keyValue instanceof String){
            if (!((String) keyValue).trim().startsWith("'") && !((String) keyValue).trim().endsWith("'")) {
                keyValue = "'" + keyValue + "'";
            }
        } else {
            keyValue = keyValue.toString();
        }
        return this.readFirstFeedByFilter(entitySetName, key+" eq "+keyValue,isOther, oDataSource);
    }

    public ODataEntry readEntry(String entitySetName, String keyValue, String expandRelationName ,String filter , boolean isOther)
            throws IOException, ODataException {
        // create absolute uri based on service uri, entity set name with its key property value and optional expanded relation name
        String absolutUri = createUri(serviceUrl, entitySetName, keyValue, expandRelationName , filter,engName);

        InputStream content = execute(absolutUri, usedFormat, HTTP_METHOD_GET);

        //System.out.println(new BufferedReader(new InputStreamRea der(content, "UTF-8")).lines().collect(Collectors.toList()).get(0));

        return entityProviderReadEntity(entitySetName, content);
    }

    private InputStream logRawContent(String prefix, InputStream content, String postfix) throws IOException {
        if(PRINT_RAW_CONTENT) {
            byte[] buffer = streamToArray(content);
            //print(prefix + new String(buffer) + postfix);
            return new ByteArrayInputStream(buffer);
        }
        return content;
    }

    private byte[] streamToArray(InputStream stream) throws IOException {
        byte[] result = new byte[0];
        int BUFFER_SIZE = 8192;
        byte[] buffer = new byte[BUFFER_SIZE];
        int readCount = stream.read(buffer);
//        logger.info("*** OData stream to array - first read count length: {}", readCount);
        while(readCount >= 0) {
            byte[] innerTmp = new byte[result.length + readCount];
            System.arraycopy(result, 0, innerTmp, 0, result.length);
            System.arraycopy(buffer, 0, innerTmp, result.length, readCount);
            result = innerTmp;
            readCount = stream.read(buffer, 0, BUFFER_SIZE);
        }
        stream.close();
//        logger.info("*** OData stream to array - result length: {}", result.length);
        return result;

        // other solution
        //return IOUtils.toByteArray(stream);
    }

    public ODataEntry createEntry(Edm edm, String serviceUri, String contentType,
                                  String entitySetName, Map<String, Object> data) throws Exception {
        String absolutUri = createUri(serviceUri, entitySetName, null , null,null,engName);
        return writeEntity(edm, absolutUri, entitySetName, data, contentType, HTTP_METHOD_POST);
    }

    public void updateEntry(Edm edm, String serviceUri, String contentType, String entitySetName,
                            String id, Map<String, Object> data) throws Exception {
        String absolutUri = createUri(serviceUri, entitySetName, id,null,null,engName);
        writeEntity(edm, absolutUri, entitySetName, data, contentType, HTTP_METHOD_PUT);
    }

    public HttpStatusCodes deleteEntry(String serviceUri, String entityName, String id) throws IOException {
        String absolutUri = createUri(serviceUri, entityName, id,null,null,engName);
        HttpURLConnection connection = connect(absolutUri, APPLICATION_XML, HTTP_METHOD_DELETE);
        return HttpStatusCodes.fromStatusCode(connection.getResponseCode());
    }


    private ODataEntry writeEntity(Edm edm, String absolutUri, String entitySetName,
                                   Map<String, Object> data, String contentType, String httpMethod)
            throws EdmException, MalformedURLException, IOException, EntityProviderException, URISyntaxException {

        HttpURLConnection connection = initializeConnection(absolutUri, contentType, httpMethod);

        EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
        EdmEntitySet entitySet = entityContainer.getEntitySet(entitySetName);
        URI rootUri = new URI(entitySetName);

        EntityProviderWriteProperties properties = EntityProviderWriteProperties.serviceRoot(rootUri).build();
        // serialize data into ODataResponse object
        ODataResponse response = EntityProvider.writeEntry(contentType, entitySet, data, properties);
        // get (http) entity which is for default Olingo implementation an InputStream
        Object entity = response.getEntity();
        if (entity instanceof InputStream) {
            byte[] buffer = streamToArray((InputStream) entity);
            // just for logging
            String content = new String(buffer);
            print(httpMethod + " request on uri '" + absolutUri + "' with content:\n  " + content + "\n");
            //
            connection.getOutputStream().write(buffer);
        }

        // if a entity is created (via POST request) the response body contains the new created entity
        ODataEntry entry = null;
        HttpStatusCodes statusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
        if(statusCode == HttpStatusCodes.CREATED) {
            // get the content as InputStream and de-serialize it into an ODataEntry object
            InputStream content = connection.getInputStream();
            content = logRawContent(httpMethod + " request on uri '" + absolutUri + "' with content:\n  ", content, "\n");
            entry = EntityProvider.readEntry(contentType,
                    entitySet, content, EntityProviderReadProperties.init().build());
        }

        //
        connection.disconnect();

        return entry;
    }

    private HttpStatusCodes checkStatus(HttpURLConnection connection) throws IOException {
        HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
//        logger.info("*** OData check status - http status code result is: " + httpStatusCode);

        if (httpStatusCode == HttpStatusCodes.NOT_FOUND && needToReloadMetaData) {
            try {
//                logger.info("Http connection response code is not found - call update edm method.");
                updateEdm();
                needToReloadMetaData = true;
            } catch (ODataException | InvalidConfigurationException e) {
//                logger.error("An error occurred while update edm metadata.", e);
                e.printStackTrace();
            }
            httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
            if (httpStatusCode == HttpStatusCodes.NOT_FOUND) {
                needToReloadMetaData = false;
            }
        }

        if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599) {
            byte[] buffer = streamToArray(connection.getErrorStream());
            String errorSt = new String(buffer);
            throw new RuntimeException("Http Connection failed with status " + httpStatusCode.getStatusCode() + " " + httpStatusCode.toString() + ", error is: " + errorSt);
        }
        return httpStatusCode;
    }

    private EdmEntityContainer checkAndUpdateEntityContainer(String entitySetName) throws IOException, ODataException {
        EdmEntityContainer entityContainer = EDM.getDefaultEntityContainer();
        if (entityContainer.getEntitySet(entitySetName) == null && needToReloadEntitySet) {
            try {
//                logger.info("Could not find entity with name: '{}' - call update edm method.", entitySetName);
                updateEdm();
                entityContainer = EDM.getDefaultEntityContainer();
            } catch (InvalidConfigurationException e) {
//                logger.error("An error occurred while update edm metadata.", e);
                e.printStackTrace();
            }

            needToReloadEntitySet = entityContainer.getEntitySet(entitySetName) != null;
        }

        return entityContainer;
    }

    private ODataFeed entityProviderReadFeed(String entitySetName, InputStream content) throws IOException, ODataException {
        return entityProviderReadFeed(entitySetName, content, true);
    }

    private ODataFeed entityProviderReadFeed(String entitySetName, InputStream content, boolean needToReloadEntityContainer) throws IOException, ODataException {
//        logger.info("*** OData call search name : " + entitySetName);

        EdmEntityContainer entityContainer = checkAndUpdateEntityContainer(entitySetName);

        ODataFeed oDataFeed = null;
        try {
            oDataFeed = EntityProvider.readFeed(usedFormat,
                    entityContainer.getEntitySet(entitySetName),
                    content,
                    EntityProviderReadProperties.init().build());
        } catch (EntityProviderException e) {
            if (e.getMessage().startsWith("Illegal argument for method call with message")) {
//                logger.info("Exception in call readFeed for '" + entitySetName + "' maybe changed entity - update entity container.");
                e.printStackTrace();
                if (needToReloadEntityContainer) {
                    try {
                        updateEdm();
                        content.reset();
                        oDataFeed = entityProviderReadFeed(entitySetName, content, false);

                        return oDataFeed;
                    } catch (InvalidConfigurationException ex) {
//                        logger.error("An error occurred while update edm metadata.", e);
                        e.printStackTrace();
                    }
                }
            }

//            logger.error("Exception in call readFeed for '" + entitySetName + "'.", e.getMessage());
            throw new RuntimeException("Exception in call readFeed for '" + entitySetName + "'.", e);
        } finally {
            if (content != null) {
                content.close();
            }
        }

        /*if (oDataFeed != null && oDataFeed.getEntries() != null) {
            logger.info("*** OData oDataFeed result size is : " + oDataFeed.getEntries().size() + " for search : " + entitySetName);
        } else {
            logger.info("*** OData oDataFeed result is null for search : " + entitySetName);
        }*/

        return oDataFeed;
    }

    private ODataEntry entityProviderReadEntity(String entitySetName, InputStream content) throws IOException, ODataException {
//        logger.info("*** OData call search name : " + entitySetName);

        EdmEntityContainer entityContainer = checkAndUpdateEntityContainer(entitySetName);

        ODataEntry oDataEntry;
        try {
            oDataEntry = EntityProvider.readEntry(usedFormat,
                    entityContainer.getEntitySet(entitySetName),
                    content,
                    EntityProviderReadProperties.init().build());
        } finally {
            if (content != null) {
                content.close();
            }
        }

        return oDataEntry;
    }

    private String createUri(String serviceUri, String entitySetName, String id, String expand , String filter , String connectionName) throws UnsupportedEncodingException {
        return createUri(serviceUri, entitySetName, id, expand, filter, connectionName, "");
    }

    private String createUri(String serviceUri, String entitySetName, String id, String expand , String filter , String connectionName, String option) throws UnsupportedEncodingException {
        final StringBuilder absolutUri = new StringBuilder(serviceUri).append(SEPARATOR).append(entitySetName);
        if(id != null) {
            absolutUri.append("(").append(id).append(")");
            //absolutUri.append("?$format=json");
        }
        if(expand != null) {
            absolutUri.append("/?$expand=").append(expand);
        }
        if(filter != null) {
            filter = URLEncoder.encode(filter , "UTF-8");
            if(expand != null) {
                absolutUri.append("$filter=").append(filter);
            } else {
                absolutUri.append("?$filter=").append(filter);
            }
            absolutUri.append("&$format=json");
        }
        if (filter!=null || expand!=null){
            absolutUri.append("&$serverName=").append(connectionName);
        } else{
            absolutUri.append("/?$serverName=").append(connectionName);
        }
        try {
//            UserVo userVo = processService.getCurrentUserDataWithoutRoleAndPost();
            if (TOKEN != null) {
                absolutUri.append("&$token=").append(TOKEN);
            }
        } catch (Exception e) {
//            logger.error("An error occurred while getting current user data to get token from it.", e);
            e.printStackTrace();
        }
        try {
            /*UserVo userVo = processService.getCurrentUserData();
            if (userVo != null && !isNullOrEmpty(userVo.getId_token())) {
                absolutUri.append("&$idtoken=").append(userVo.getId_token());
            }*/
        } catch (Exception e) {
//            logger.error("An error occurred while getting current user data to get id_token from it.", e);
            e.printStackTrace();
        }
        if (!isNullOrEmpty(option)) {
            if (expand == null && filter == null && connectionName == null) {
                absolutUri.append("?").append(option);
            } else {
                absolutUri.append("&").append(option);
            }
        }
        return absolutUri.toString().replaceAll("\\+","%20");
    }

    private InputStream execute(String relativeUri, String contentType, String httpMethod) throws IOException {
       /* logger.info("*** OData execute :"+relativeUri+" ***");
        PerformanceLogObject performanceLogObject = new PerformanceLogObject();
        try {
            performanceLogObject = processService.getPerformanceLogObject();
            performanceLogObject.setCurrentTime(System.currentTimeMillis());
            performanceLogObject.setLogMessageType(PerformanceLogObject.LogMessageType.ODATA_STARTED);
            performanceLogger.trace(JsonUtil.getJson(performanceLogObject));
        } catch (Exception e) {
            logger.warn("An error occurred while getting performance log object and logging.", e);
        }*/
        HttpURLConnection connection = null;
        InputStream content = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            connection = connect(relativeUri, contentType, httpMethod);
            content = connection.getInputStream();
            content = logRawContent(httpMethod + " request on uri '" + relativeUri + "' with content:\n  ", content, "\n");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        /*try {
            performanceLogObject.setCurrentTime(System.currentTimeMillis());
            performanceLogObject.setLogMessageType(PerformanceLogObject.LogMessageType.ODATA_ENDED);
            performanceLogger.trace(JsonUtil.getJson(performanceLogObject));
        } catch (Exception e) {
            logger.warn("An error occurred while getting performance log object and logging.", e);
        }*/
        return content;
    }

    private HttpURLConnection connect(String relativeUri, String contentType, String httpMethod) throws IOException {
        HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);

        //connection.connect();
        checkStatus(connection);

        return connection;
    }

    private HttpURLConnection initializeConnection(String absolutUri, String contentType, String httpMethod)
            throws MalformedURLException, IOException {
        URL url = new URL(absolutUri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
       /* if (connection == null) {
            logger.warn("*** OData http connection is null !");
            throw new RuntimeException("*** OData http connection is null !");
        } else {
            logger.info("*** OData http connection is connected - " + absolutUri);
        }*/

        connection.setUseCaches(false);
        connection.setRequestMethod(httpMethod);
        connection.setRequestProperty(HttpHeaders.ACCEPT, contentType);
        if (HTTP_METHOD_POST.equals(httpMethod) || HTTP_METHOD_PUT.equals(httpMethod)) {
            connection.setDoOutput(true);
            connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, contentType);
        }

        return connection;
    }

    private boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public enum ODataSource {

        APP(""),
        ENGINE("_eng"),
        FANITORING("_fan"),
        MARTA("_marta");

        private final String value;

        ODataSource(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}