package com.fanap.fanrp.pat.patemailserver.dto;

import com.fanap.fanrp.pat.patemailserver.utils.Hashable;

public class PATRelatedLetterDTO extends Hashable {
    private PATCategoryElementDTO relatedLetterType;
    private PATLetterDTO letter;
    private String letterNo;
    private long date;
    private String releatedLetterNo;
    private String type;

    public PATCategoryElementDTO getRelatedLetterType() {
        return relatedLetterType;
    }

    public void setRelatedLetterType(PATCategoryElementDTO relatedLetterType) {
        this.relatedLetterType = relatedLetterType;
    }

    public PATLetterDTO getLetter() {
        return letter;
    }

    public void setLetter(PATLetterDTO letter) {
        this.letter = letter;
    }

    public String getLetterNo() {
        return letterNo;
    }

    public void setLetterNo(String letterNo) {
        this.letterNo = letterNo;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getReleatedLetterNo() {
        return releatedLetterNo;
    }

    public void setReleatedLetterNo(String releatedLetterNo) {
        this.releatedLetterNo = releatedLetterNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
