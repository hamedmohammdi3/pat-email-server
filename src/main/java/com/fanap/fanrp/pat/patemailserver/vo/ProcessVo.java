package com.fanap.fanrp.pat.patemailserver.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProcessVo implements Serializable {
    private static final long serialVersionUID = 3691196179187381242L;
    private Long parentProcessInstanceId;
    private ProcessVo parentProcessVo;
    //the grand goal
    private Long processTokenId;
    private Long parentProcessTokenId;
    private Long rootBP;
    private String processClassName;
    private String processCode; // Class name
    private String processName; // Persian name
    private Long processId;             // id in ProcessData table
    private Long processInstanceId;     // instanceId in ProcessInstanceData table
    private Long threadIndex;
    private String nodeId;
    private String nodeName;
    private String processStatus;
    private String currerntLaneQuery;
    private String product;
    private String processType;
    private String dataContent; // can be: ViewableDataInfoVO, CartableMetaInfo, StartElementVo
    private int commentsCount;
    private List<ProcessVo> subProcessList = new ArrayList<>();
    private String schemaVersion;
    private int bundleVersion;
    private String searchKey;
    private HashMap<String, String> searchParameters = new HashMap<>();
    private Long startTime;     // process creation time
    private Long changeTime;
    private boolean userHasPermission;  // user can do action on process or not
    private boolean fromSearch;
    private HashMap<String, String> taskActions = new HashMap<>();
    private String forwardPersonId;
    private boolean read;
    private String engineName;
    private String processTrackerId;//for simulator
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(Long parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public ProcessVo getParentProcessVo() {
        return parentProcessVo;
    }

    public void setParentProcessVo(ProcessVo parentProcessVo) {
        this.parentProcessVo = parentProcessVo;
    }

    public Long getProcessTokenId() {
        return processTokenId;
    }

    public void setProcessTokenId(Long processTokenId) {
        this.processTokenId = processTokenId;
    }

    public Long getParentProcessTokenId() {
        return parentProcessTokenId;
    }

    public void setParentProcessTokenId(Long parentProcessTokenId) {
        this.parentProcessTokenId = parentProcessTokenId;
    }

    public Long getRootBP() {
        return rootBP;
    }

    public void setRootBP(Long rootBP) {
        this.rootBP = rootBP;
    }

    public String getProcessClassName() {
        return processClassName;
    }

    public void setProcessClassName(String processClassName) {
        this.processClassName = processClassName;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Long getThreadIndex() {
        return threadIndex;
    }

    public void setThreadIndex(Long threadIndex) {
        this.threadIndex = threadIndex;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getCurrerntLaneQuery() {
        return currerntLaneQuery;
    }

    public void setCurrerntLaneQuery(String currerntLaneQuery) {
        this.currerntLaneQuery = currerntLaneQuery;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getDataContent() {
        return dataContent;
    }

    public void setDataContent(String dataContent) {
        this.dataContent = dataContent;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public List<ProcessVo> getSubProcessList() {
        return subProcessList;
    }

    public void setSubProcessList(List<ProcessVo> subProcessList) {
        this.subProcessList = subProcessList;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public int getBundleVersion() {
        return bundleVersion;
    }

    public void setBundleVersion(int bundleVersion) {
        this.bundleVersion = bundleVersion;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public HashMap<String, String> getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(HashMap<String, String> searchParameters) {
        this.searchParameters = searchParameters;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Long changeTime) {
        this.changeTime = changeTime;
    }

    public boolean isUserHasPermission() {
        return userHasPermission;
    }

    public void setUserHasPermission(boolean userHasPermission) {
        this.userHasPermission = userHasPermission;
    }

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public HashMap<String, String> getTaskActions() {
        return taskActions;
    }

    public void setTaskActions(HashMap<String, String> taskActions) {
        this.taskActions = taskActions;
    }

    public String getForwardPersonId() {
        return forwardPersonId;
    }

    public void setForwardPersonId(String forwardPersonId) {
        this.forwardPersonId = forwardPersonId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getProcessTrackerId() {
        return processTrackerId;
    }

    public void setProcessTrackerId(String processTrackerId) {
        this.processTrackerId = processTrackerId;
    }
}