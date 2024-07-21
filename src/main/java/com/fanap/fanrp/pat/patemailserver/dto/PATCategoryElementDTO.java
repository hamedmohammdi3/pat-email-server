package com.fanap.fanrp.pat.patemailserver.dto;

import com.fanap.fanrp.pat.patemailserver.utils.Hashable;

public class PATCategoryElementDTO extends Hashable {
    private String categoryElementId;
    private String value;
    private String code;

    public String getCategoryElementId() {
        return categoryElementId;
    }

    public void setCategoryElementId(String categoryElementId) {
        this.categoryElementId = categoryElementId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}