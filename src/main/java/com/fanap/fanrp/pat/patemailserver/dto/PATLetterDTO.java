package com.fanap.fanrp.pat.patemailserver.dto;


import com.fanap.fanrp.pat.patemailserver.core.message.EmailRaw;
import com.fanap.fanrp.pat.patemailserver.utils.Hashable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PATLetterDTO extends Hashable {

    private ArrayList<PATAttachmentDTO> attachmentSet = new ArrayList();
    private String subject;
    private PATCategoryElementDTO forAction;
    private ArrayList<PATRelatedLetterDTO> relatedLetter = new ArrayList();
    private String oldLetterNo;
    private Timestamp registerDate;
    private Long templateId;
    private PATCategoryElementDTO security;
    private ArrayList<PATObjectDTO> patObjectDTOS = new ArrayList<>();
    private String letterDate;
    private Map<String, String> additionalData = new HashMap<>();

    private Timestamp date;
    private String vrToDraft;
    private Boolean isTemplate;
    private String letterId;
    private PATCategoryElementDTO docType;
    private PATCategoryElementDTO priority;
    private ArrayList<PATContactDTO> ccPersonSet = new ArrayList();
    private String vrCreator;
    private PATCategoryElementDTO letterDirection;
    private Timestamp deadline;
    private PATContactDTO signer;
    private String letterNo;
    private PATSecretariatInfoDTO secret;
    private String bodyFile;
    private String keywords;
    private String docFile;
    private PATCategoryElementDTO letterType;
    private ArrayList<PATContactDTO> toPersonSet = new ArrayList();
    private ArrayList<PATContactDTO> bccPersonSet = new ArrayList();
    private String actionType;
    private PATContactDTO fromPerson;
    private String body;
    private String reference;
    private Boolean isSubmit;

    private String taskNo;
    private ArrayList<PATContactDTO> receiverReferralDTO;
    //private ArrayList<PATContactPersonDTO> ccPersonSetDTO;
    private PATCategoryElementDTO letterCategory;
    //private ArrayList<PATContactPersonDTO> toPersonSetDTO;
    private ArrayList<PATContactDTO> toOrgSet;
    //private ArrayList<PATContactPersonDTO> bccPersonSetDTO;
    private PATContactPersonDTO fromPersonDTO;

    public PATLetterDTO of (EmailRaw emailRaw){
return null;
    }

    public String getOldLetterNo() {
        return oldLetterNo;
    }

    public void setOldLetterNo(String oldLetterNo) {
        this.oldLetterNo = oldLetterNo;
    }

    public ArrayList<PATObjectDTO> getPatObjectDTOS() {
        return patObjectDTOS;
    }

    public void setPatObjectDTOS(ArrayList<PATObjectDTO> patObjectDTOS) {
        this.patObjectDTOS = patObjectDTOS;
    }


    public ArrayList<PATAttachmentDTO> getAttachmentSet() {
        return attachmentSet;
    }

    public void setAttachmentSet(ArrayList<PATAttachmentDTO> attachmentSet) {
        this.attachmentSet = attachmentSet;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public PATCategoryElementDTO getForAction() {
        return forAction;
    }

    public void setForAction(PATCategoryElementDTO forAction) {
        this.forAction = forAction;
    }

    public ArrayList<PATRelatedLetterDTO> getRelatedLetter() {
        return relatedLetter;
    }

    public void setRelatedLetter(ArrayList<PATRelatedLetterDTO> relatedLetter) {
        this.relatedLetter = relatedLetter;
    }

    public String getLetterId() {
        return letterId;
    }

    public void setLetterId(String letterId) {
        this.letterId = letterId;
    }

    public Timestamp getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Timestamp registerDate) {
        this.registerDate = registerDate;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getVrToDraft() {
        return vrToDraft;
    }

    public void setVrToDraft(String vrToDraft) {
        this.vrToDraft = vrToDraft;
    }

    public Boolean getTemplate() {
        return isTemplate;
    }

    public void setTemplate(Boolean template) {
        isTemplate = template;
    }

    public PATCategoryElementDTO getDocType() {
        return docType;
    }

    public void setDocType(PATCategoryElementDTO docType) {
        this.docType = docType;
    }

    public PATCategoryElementDTO getPriority() {
        return priority;
    }

    public void setPriority(PATCategoryElementDTO priority) {
        this.priority = priority;
    }

    public ArrayList<PATContactDTO> getCcPersonSet() {
        return ccPersonSet;
    }

    public void setCcPersonSet(ArrayList<PATContactDTO> ccPersonSet) {
        this.ccPersonSet = ccPersonSet;
    }

    public String getVrCreator() {
        return vrCreator;
    }

    public void setVrCreator(String vrCreator) {
        this.vrCreator = vrCreator;
    }

    public PATCategoryElementDTO getLetterDirection() {
        return letterDirection;
    }

    public void setLetterDirection(PATCategoryElementDTO letterDirection) {
        this.letterDirection = letterDirection;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public PATContactDTO getSigner() {
        return signer;
    }

    public void setSigner(PATContactDTO signer) {
        this.signer = signer;
    }

    public String getLetterNo() {
        return letterNo;
    }

    public void setLetterNo(String letterNo) {
        this.letterNo = letterNo;
    }

    public PATSecretariatInfoDTO getSecret() {
        return secret;
    }

    public void setSecret(PATSecretariatInfoDTO secret) {
        this.secret = secret;
    }

    public String getBodyFile() {
        return bodyFile;
    }

    public void setBodyFile(String bodyFile) {
        this.bodyFile = bodyFile;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDocFile() {
        return docFile;
    }

    public void setDocFile(String docFile) {
        this.docFile = docFile;
    }

    public PATCategoryElementDTO getLetterType() {
        return letterType;
    }

    public void setLetterType(PATCategoryElementDTO letterType) {
        this.letterType = letterType;
    }

    public ArrayList<PATContactDTO> getToPersonSet() {
        return toPersonSet;
    }

    public void setToPersonSet(ArrayList<PATContactDTO> toPersonSet) {
        this.toPersonSet = toPersonSet;
    }

    public ArrayList<PATContactDTO> getBccPersonSet() {
        return bccPersonSet;
    }

    public void setBccPersonSet(ArrayList<PATContactDTO> bccPersonSet) {
        this.bccPersonSet = bccPersonSet;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public PATContactDTO getFromPerson() {
        return fromPerson;
    }

    public void setFromPerson(PATContactDTO fromPerson) {
        this.fromPerson = fromPerson;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public PATCategoryElementDTO getSecurity() {
        return security;
    }

    public void setSecurity(PATCategoryElementDTO security) {
        this.security = security;
    }

    public Boolean getSubmit() {
        return isSubmit;
    }

    public void setSubmit(Boolean submit) {
        isSubmit = submit;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public ArrayList<PATContactDTO> getReceiverReferralDTO() {
        return receiverReferralDTO;
    }

    public void setReceiverReferralDTO(ArrayList<PATContactDTO> receiverReferralDTO) {
        this.receiverReferralDTO = receiverReferralDTO;
    }

    public ArrayList<PATContactDTO> getToOrgSet() {
        return toOrgSet;
    }

    public void setToOrgSet(ArrayList<PATContactDTO> toOrgSet) {
        this.toOrgSet = toOrgSet;
    }

    public PATCategoryElementDTO getLetterCategory() {
        return letterCategory;
    }

    public void setLetterCategory(PATCategoryElementDTO letterCategory) {
        this.letterCategory = letterCategory;
    }


    public Map<String, String> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, String> additionalData) {
        this.additionalData = additionalData;
    }

    public PATContactPersonDTO getFromPersonDTO() {
        return fromPersonDTO;
    }

    public void setFromPersonDTO(PATContactPersonDTO fromPersonDTO) {
        this.fromPersonDTO = fromPersonDTO;
    }

    public String getLetterDate() {
        return letterDate;
    }

    public void setLetterDate(String letterDate) {
        this.letterDate = letterDate;
    }
}

//letterDirection  nullable = false