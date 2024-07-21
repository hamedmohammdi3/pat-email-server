package com.fanap.fanrp.pat.patemailserver.dto;

import com.fanap.fanrp.pat.patemailserver.utils.Hashable;

import java.sql.Timestamp;
import java.util.ArrayList;

public class PATContactPersonDTO extends Hashable {

    private Long personId;
    private String vrTitle;
    private String vrOrganization;
    private String vrOrganizationId;
    private String vrPost;
    private String roleOutsideId;
    private String vrPostId;
    private String personName;
    private Long deadline;
    private String forActionFlow;
    private String description;
    private String personType;
    private String jobTitle;
    private String rowType;
    private Integer labor;
    private String vrPerson;
    private String deliveryInfo;
    private String signature;
    private Integer order;
    private Timestamp deliveryDate;
    private String personDeliveryTypeCode;
    private String personDeliveryTypeValue;
    private String sign;
    private String forActionFlowCode;
    private String additionalDescription;
    private PATCategoryElementDTO referralrePriority;
    private ArrayList<PATAttachmentDTO> attach;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getVrTitle() {
        return vrTitle;
    }

    public void setVrTitle(String vrTitle) {
        this.vrTitle = vrTitle;
    }

    public String getVrOrganization() {
        return vrOrganization;
    }

    public void setVrOrganization(String vrOrganization) {
        this.vrOrganization = vrOrganization;
    }

    public String getVrOrganizationId() {
        return vrOrganizationId;
    }

    public void setVrOrganizationId(String vrOrganizationId) {
        this.vrOrganizationId = vrOrganizationId;
    }

    public String getVrPost() {
        return vrPost;
    }

    public void setVrPost(String vrPost) {
        this.vrPost = vrPost;
    }

    public String getRoleOutsideId() {
        return roleOutsideId;
    }

    public void setRoleOutsideId(String roleOutsideId) {
        this.roleOutsideId = roleOutsideId;
    }

    public String getVrPostId() {
        return vrPostId;
    }

    public void setVrPostId(String vrPostId) {
        this.vrPostId = vrPostId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public String getForActionFlow() {
        return forActionFlow;
    }

    public void setForActionFlow(String forActionFlow) {
        this.forActionFlow = forActionFlow;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getRowType() {
        return rowType;
    }

    public void setRowType(String rowType) {
        this.rowType = rowType;
    }

    public Integer getLabor() {
        return labor;
    }

    public void setLabor(Integer labor) {
        this.labor = labor;
    }

    public String getVrPerson() {
        return vrPerson;
    }

    public void setVrPerson(String vrPerson) {
        this.vrPerson = vrPerson;
    }

    public String getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(String deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Timestamp getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Timestamp deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPersonDeliveryTypeCode() {
        return personDeliveryTypeCode;
    }

    public void setPersonDeliveryTypeCode(String personDeliveryTypeCode) {
        this.personDeliveryTypeCode = personDeliveryTypeCode;
    }

    public String getPersonDeliveryTypeValue() {
        return personDeliveryTypeValue;
    }

    public void setPersonDeliveryTypeValue(String personDeliveryTypeValue) {
        this.personDeliveryTypeValue = personDeliveryTypeValue;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getForActionFlowCode() {
        return forActionFlowCode;
    }

    public void setForActionFlowCode(String forActionFlowCode) {
        this.forActionFlowCode = forActionFlowCode;
    }

    public String getAdditionalDescription() {
        return additionalDescription;
    }

    public void setAdditionalDescription(String additionalDescription) {
        this.additionalDescription = additionalDescription;
    }

    public PATCategoryElementDTO getReferralrePriority() {
        return referralrePriority;
    }

    public void setReferralrePriority(PATCategoryElementDTO referralrePriority) {
        this.referralrePriority = referralrePriority;
    }

    public ArrayList<PATAttachmentDTO> getAttach() {
        return attach;
    }

    public void setAttach(ArrayList<PATAttachmentDTO> attach) {
        this.attach = attach;
    }
}