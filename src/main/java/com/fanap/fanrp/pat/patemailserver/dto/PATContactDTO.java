package com.fanap.fanrp.pat.patemailserver.dto;

import com.fanap.fanrp.pat.patemailserver.utils.Hashable;

import java.sql.Timestamp;
import java.util.ArrayList;

public class PATContactDTO extends Hashable {
    private PATCategoryElementDTO personDeliveryType;
    private String vrPerson;
    private String deliveryInfo;
    private String description;
    private String signature;
    private String vrPost;
    private Timestamp deadline;
    private String personType;
    private String vrTitle;
    private String rowType;
    private Integer labor;
    private Integer order;
    private String vrPostId;
    private String vrOrganization;
    private boolean publicAttachment;
    private String additionalDescription;
    private ArrayList<PATAttachmentDTO> attachReceiverReferralSet;
    private PATCategoryElementDTO referralPriority;

    public PATCategoryElementDTO getPersonDeliveryType() {
        return personDeliveryType;
    }

    public void setPersonDeliveryType(PATCategoryElementDTO personDeliveryType) {
        this.personDeliveryType = personDeliveryType;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getVrPost() {
        return vrPost;
    }

    public void setVrPost(String vrPost) {
        this.vrPost = vrPost;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getVrTitle() {
        return vrTitle;
    }

    public void setVrTitle(String vrTitle) {
        this.vrTitle = vrTitle;
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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getVrPostId() {
        return vrPostId;
    }

    public void setVrPostId(String vrPostId) {
        this.vrPostId = vrPostId;
    }

    public String getVrOrganization() {
        return vrOrganization;
    }

    public void setVrOrganization(String vrOrganization) {
        this.vrOrganization = vrOrganization;
    }

    public boolean isPublicAttachment() {
        return publicAttachment;
    }

    public void setPublicAttachment(boolean publicAttachment) {
        this.publicAttachment = publicAttachment;
    }

    public String getAdditionalDescription() {
        return additionalDescription;
    }

    public void setAdditionalDescription(String additionalDescription) {
        this.additionalDescription = additionalDescription;
    }

    public ArrayList<PATAttachmentDTO> getAttachReceiverReferralSet() {
        return attachReceiverReferralSet;
    }

    public void setAttachReceiverReferralSet(ArrayList<PATAttachmentDTO> attachReceiverReferralSet) {
        this.attachReceiverReferralSet = attachReceiverReferralSet;
    }

    public PATCategoryElementDTO getReferralPriority() {
        return referralPriority;
    }

    public void setReferralPriority(PATCategoryElementDTO referralPriority) {
        this.referralPriority = referralPriority;
    }
}