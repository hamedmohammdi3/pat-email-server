package com.fanap.fanrp.pat.patemailserver.dto;

import com.fanap.fanrp.pat.patemailserver.utils.Hashable;

public class PATAttachmentDTO extends Hashable {
    private String vrAttachmentDocument;
    private String description;
    private String latinHeadline;

    public String getVrAttachmentDocument() {
        return vrAttachmentDocument;
    }

    public void setVrAttachmentDocument(String vrAttachmentDocument) {
        this.vrAttachmentDocument = vrAttachmentDocument;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatinHeadline() {
        return latinHeadline;
    }

    public void setLatinHeadline(String latinHeadline) {
        this.latinHeadline = latinHeadline;
    }
}