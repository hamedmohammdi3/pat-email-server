package com.fanap.fanrp.pat.patemailserver.dto;

import com.fanap.fanrp.pat.patemailserver.utils.Hashable;

import java.sql.Timestamp;
import java.util.ArrayList;

public class PATSecretariatInfoDTO  extends Hashable {
    private String deliveryReceiptNo;
    private Boolean systemPerson = ((Boolean) false);
    private String deliveryInfo;
    private Timestamp registerDate;
    private String vrPerson;
    private String indicatorNo;
    private Timestamp deliveryDate;
    private PATCategoryElementDTO deliveryType;
    private String personContactInfo;
    private PATContactPersonDTO sender;
    private ArrayList<PATAttachmentDTO> secretAttachSet = new ArrayList();
    private String sendCode;
    private String fromEmail;

}