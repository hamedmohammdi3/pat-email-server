package com.fanap.fanrp.pat.patemailserver.enums;

public enum MessageType {

    CUSTOME_LOGIN,
    UNKNOWN,
    // Push messages
    PROCESS_STATUS(DeliveryType.INVOLVED_USERS),
    ERROR,
    GET_INPUT,
    SHOW_OUTPUT,
    END_PROCESS(DeliveryType.INVOLVED_USERS),
    SEND_AVAILABLE_PROCESS,
    TEXT_MESSAGE(DeliveryType.REQUESTER),
    SEND_CARTABLE_ITEMS,
    SEND_TASKLIST,
    GET_MENU_USAGE,
    SEND_MENU_USAGE,
    LOGIN_RESPONSE(DeliveryType.REQUESTER),
    WAIT_TO_PROCESS_CALL,
    CANCEL_GET_INPUT,//Soleimani: to cancel the get input process
    CHANGE_PASSWORD_RESPONSE,
    PROCESS_TERMINATED,//Soleimani: to terminate process
    SEND_COMMENTS,
    BOUNDARY_EVENT_CAUGHT,//Soleimani
    REQUEST_SUCCESSFUL,
    SEARCH_CALL,//Maghsoud
    GET_ERROR_MESSAGE,//Request of sending exception messages to ui
    SEND_ERROR_MESSAGE,//Sending exception messages to ui
    GET_PROCESS_STATUS, // for sdk


    // Messages from UI
    BACK_PROCESS(true),
    TIMEOUT_PROCESS(true),    // from IOManager
    SEND_PREVIEW,
    SEND_INPUT(true),
    NEXT_INPUT(true),
    INIT_PROCESS,
    INIT_SUB_PROCESS,
    GET_AVAILABLE_PROCESS,  // previous: GET_DEPLOYED_PROCESS
    SEND_DEPLOYED_PROCESS,
    GET_DEPLOYED_PROCESS,
    CONTINUE_PROCESS(true),
    GET_CARTABLE_ITEMS,
    LOGIN_REQUEST,
    LOGOUT_REQUEST,
    CHANGE_PASSWORD_REQUEST,
    ABORT_PROCESS(true),//Soleimani: to abort the process
    RESTART_PROCESS(true),//Soleimani: to restart the process
    POST_COMMENT,
    GET_COMMENTS,
    GET_TASKLIST,
    USER_POSTS,
    GET_SUBPROCESSES,
    GET_PRODUCTS_CARDS,
    REMOVE_CARD,
    GET_TP_CARDS,
    GET_PROCESS_FAVORITE_LIST,
    SEND_PROCESS_FAVORITE,
    CHECK_AUTHENTICATE_TOKEN,

    //Engine to Engine
    SEND_PROCESS_MESSAGE,
    GET_MESSAGE_SCHEMA,


    // From ActionType
    SHOW_INFO, SHOW_ERROR, CATCH_EVENT, INTERRUPTED_BY_SUBPROCESS, INTERRUPTED_BY_BOUNDARY_EVENT,

    // From other servers
    CHAT_MESSAGE,
    TELEGRAM_MESSAGE,
    EMAIL_MESSAGE,
    REGISTER_EMAIL_REQUEST,
    GET_EMAIL,
    WATCHED_EMAIL,
    FETCH_EMAIL_REQUEST,
    FETCH_EMAIL_RESPONSE,
    REGISTER_EMAIL_RESPONSE,
    // Loader request messages from UI
    SQL_LOAD,
    GET_UI_SCHEMA,
    GET_UI_SCHEMA_VERSION,
    GET_UI_LANG_BUNDLE,
    GET_UI_LANG_BUNDLE_VERSION,
    UPDATE_UI_LANG_BUNDLE_VERSION,
    GET_USER_CONFIG,
    SET_USER_CONFIG,
    EXECUTE_REMOTE_SERVICE,
    ALL_LOCALES,

    // Loader response messages
    SQL_RESULT,
    UI_SCHEMA_RESULT,
    UI_SCHEMA_VERSION_RESULT,
    UI_LANG_BUNDLE_RESULT,
    UI_LANG_BUNDLE_VERSION,
    USER_CONFIG_RESULT,
    EXECUTE_REMOTE_SERVICE_RESULT,

    // Profile Manager
    SAVE_VIEW_PROFILE,
    DELETE_VIEW,
    SAVE_CATEGORY_VIEW_PROFILE,
    DELETE_CATEGORY_VIEW,
    GET_VIEW_SEARCHABLEFIELD,
    /*GET_VIEW_BY_USERNAME,
    GET_VIEW_BY_ID,
    GET_VIEW_BY_DEFAULT,*/
    SEND_VIEWS,
    VIEW_INSERT_RESULT,
    VIEW_DELETE_RESULT,
    CAREGORY_VIEW_INSERT_RESULT,
    CAREGORY_VIEW_DELETE_RESULT,
    VIEW_SEARCHABLEFIELD_RESULT,
    GET_VIEWS,// for get all Views and save new Profile !!!
    GET_CAREGORY_VIEWS,


    // Forward Action
    SAVE_FORWARD_DATA,
    DELETE_FORWARD_DATA,
    GET_FORWARD_DATA,
    FORWARD_SAVE_RESULT,
    FORWARD_DELETE_RESULT,
    SEND_FORWARD_DATA,
    FORWARD_ACCEPT,
    FORWARD_PROCESS(true),

    // REPLACE user process Action
    SAVE_REPLACEMENT_PROCESS_DATA,
    DELETE_REPLACEMENT_PROCESS_DATA,
    GET_REPLACEMENT_PROCESS_DATA,
    REPLACEMENT_PROCESS_SAVE_RESULT,
    REPLACEMENT_PROCESS_DELETE_RESULT,
    SEND_REPLACEMENT_PROCESS_DATA,
    REPLACEMENT_PROCESS_ACCEPT,
    REPLACEMENT_PROCESS,

    // user Profile
    SAVE_USER_PROFILE,
    DELETE_USER_PROFILE,
    GET_USER_PROFILE_All,
    GET_USER_PROFILE_BY_USERNAME,
    GET_USER_PROFILE_BY_PERSONNELCODE,
    GET_USER_PROFILE_BY_ID,
    USER_PROFILE_RESULT,

    // user list for forward and reply Action in  Cartable!
    REQUEST_USER_LIST,
    SEND_USER_LIST,

    GET_PROCESS_HISTORY,
    SEND_PROCESS_HISTORY,

    GET_PROCESS_PREVIEW,
    SEND_PROCESS_PREVIEW,
    // Process List Name
    GET_PROCESS_LIST_NAME,
    SEND_PROCESS_LIST_NAME,

    //Engine Version
    GET_ENGINE_VERSION,
    SEND_ENGINE_VERSION,

    //Iteroperability
    INVOKE_SERVICE,
    INVOKE_MULTIPLE_SERVICE,
    RESPONSE_INVOKE_SERVICE,


    //For Service Gateway
    GET_ENGINE_SPECIFICATION,
    SEND_ENGINE_SPECIFICATION,
    GET_ENGINE_CONTAINS_PROCESS,
    SEND_ENGINE_CONTAINS_PROCESS,
    GET_ENGINE_CONTAINS_SERVICE,
    SEND_ENGINE_CONTAINS_SERVICE,
    GET_RUNNING_ENGINES,
    GET_SCHEMA_MAP,

    FIND_SPECIFIC_ENGINE,
    GET_ENGINES,
    GET_USER_ENGINES,
    SEND_USER_ENGINES,

    //Ou list
    GET_OU_LIST,
    SEND_OU_LIST,

    GET_ORGANIZATION_INFO,
    SEND_ORGANIZATION_INFO,

    //OData
    ODATA_SERVICE_UPDATE;


    private boolean haveToken = false;
    private DeliveryType deliveryType;

    MessageType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    MessageType() {
        this.deliveryType = DeliveryType.REQUESTER;
    }

    MessageType(boolean haveToken) {
        this();
        this.haveToken = haveToken;
    }

    MessageType(boolean haveToken, DeliveryType deliveryType) {
        this.haveToken = haveToken;
        this.deliveryType = deliveryType;
    }

    public DeliveryType getDeliveryType() {
        return this.deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public boolean getHaveToken() {
        return haveToken;
    }

    public void setHaveToken(boolean haveToken) {
        this.haveToken = haveToken;
    }

    public enum DeliveryType {
        REQUESTER,  // only deliver to the peer of requester
        PEERS_OF_REQUESTER, // all peers of user
        INVOLVED_USERS, //  all peers of involved users in Process
        EVERYBODY,   // all peers of online users
    }
}