package com.fanap.fanrp.pat.patemailserver.utils;

import com.fanap.fanrp.pat.patemailserver.core.message.AddressHeader;
import com.fanap.fanrp.pat.patemailserver.core.message.EmailAddress;
import com.fanap.fanrp.pat.patemailserver.core.message.EmailRaw;
import com.fanap.fanrp.pat.patemailserver.dto.PATAttachmentDTO;
import com.fanap.fanrp.pat.patemailserver.dto.PATCategoryElementDTO;
import com.fanap.fanrp.pat.patemailserver.dto.PATContactDTO;
import com.fanap.fanrp.pat.patemailserver.dto.PATLetterDTO;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DTOConvertor {

    public String CRLF = "\r\n";
    public String END_BODY = "--\r\n";
    public String START_BODY = "This is a multi-part message in MIME format";
    public String TEXT_HTML = "text/html";
    public String TEXT_PLAIN = "text/plain";

    private EmailRaw emailRaw;

    public DTOConvertor(EmailRaw emailRaw) {
        this.emailRaw = emailRaw;
    }

    public ArrayList<PATContactDTO> toPersonList() {
        AddressHeader toAddress = emailRaw.getData().getHeaders().getTo();
        ArrayList<PATContactDTO> patContactDTOList = fillPersonList(toAddress);
        if (patContactDTOList == null) return null;
        return patContactDTOList;
    }

    public ArrayList<PATContactDTO> ccPersonList() {
        AddressHeader ccAddress = emailRaw.getData().getHeaders().getCc();
        ArrayList<PATContactDTO> patContactDTOList = fillPersonList(ccAddress);
        if (patContactDTOList == null) return null;
        return patContactDTOList;
    }

    public ArrayList<PATContactDTO> bccPersonList() {

        List<EmailAddress> emailAddressList = emailRaw.getRecipients();
        if (emailAddressList == null || emailAddressList.isEmpty()) {
            return null;
        }

        AddressHeader toAddress = emailRaw.getData().getHeaders().getTo();
        AddressHeader ccAddress = emailRaw.getData().getHeaders().getCc();

        if (toAddress != null) {
            for (EmailAddress address : toAddress.getAddresses()) {
                emailAddressList.removeIf(r -> r.getAddress().equals(address.getAddress()));
            }
        }

        if (ccAddress != null) {
            for (EmailAddress address : ccAddress.getAddresses()) {
                emailAddressList.removeIf(r -> r.getAddress().equals(address.getAddress()));
            }
        }

        ArrayList<PATContactDTO> patContactDTOList = new ArrayList<>();
        for (EmailAddress address : emailAddressList) {
            PATContactDTO dto = new PATContactDTO();
            dto.setVrPerson(address.getMailbox());
            dto.setVrTitle(address.getAddress());
            patContactDTOList.add(dto);
        }
        return patContactDTOList;
    }

    public PATLetterDTO getLetterDTO() {
        PATLetterDTO patLetterDTO = new PATLetterDTO();
        patLetterDTO.setSubject(parseSubject());
        patLetterDTO.setBody(parseBody());

        EmailAddress senderAddress = emailRaw.getData().getHeaders().getFrom().getAddresses().get(0);
        PATContactDTO dto = new PATContactDTO();
        dto.setVrPerson(senderAddress.getMailbox());
        dto.setVrTitle(senderAddress.getAddress());

        patLetterDTO.setFromPerson(dto);
        patLetterDTO.setVrCreator(senderAddress.getMailbox());

        PATCategoryElementDTO letterDirection = new PATCategoryElementDTO();
        letterDirection.setCode("INT_OUT_LETTER");

        PATCategoryElementDTO docType = new PATCategoryElementDTO();
        docType.setCode("BYCK");

        patLetterDTO.setLetterDirection(letterDirection);
        patLetterDTO.setDocType(docType);

        patLetterDTO.setActionType("SEND");
        patLetterDTO.setToPersonSet(toPersonList());
        patLetterDTO.setCcPersonSet(ccPersonList());
        patLetterDTO.setBccPersonSet(bccPersonList());

        return patLetterDTO;
    }

    private ArrayList<PATContactDTO> fillPersonList(AddressHeader addressList) {
        if (addressList == null) {
            return null;
        }

        List<EmailAddress> emailAddressList = addressList.getAddresses();
        ArrayList<PATContactDTO> patContactDTOList = new ArrayList<>();
        for (EmailAddress address : emailAddressList) {
            PATContactDTO dto = new PATContactDTO();
            dto.setVrPerson(address.getMailbox());
            dto.setVrTitle(address.getAddress());
            patContactDTOList.add(dto);
        }
        return patContactDTOList;
    }

    private String parseSubject(){
        String subject = emailRaw.getData().getHeaders().getSubject();
        if (subject.startsWith("=?UTF-8?")){
            subject = subject.replaceAll("=\\?UTF-8\\?B\\?","");
            subject = subject.replaceAll("\\?= ","");
            //byte[] base64decodedBytes = Base64.getDecoder().decode(subject);
            try {
                String decodedString = new String(Base64.decodeBase64(subject.getBytes()),"utf-8");
                subject = decodedString ;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return subject;
    }
    private String parseBody() {

        String data = emailRaw.getData().getBody();
        int startBodySplitIndex = data.indexOf("--");
        String body = "";

        if (startBodySplitIndex == -1) {
            body = data.trim();
            return body;
        }

        int endBodySplitIndex = data.substring(startBodySplitIndex).indexOf(CRLF) + startBodySplitIndex;
        String bodySplit = data.substring(startBodySplitIndex, endBodySplitIndex);
        String[] bodyList = data.split(bodySplit);

        for (String b : bodyList) {
            if (b.contains("text/html")) {
                body = b.trim().replace("Content-Type: text/html; charset=utf-8", "");
                body = body.replace("Content-Transfer-Encoding: 7bit", "");
                body = body.replace("Content-Transfer-Encoding: 8bit", "");
                body = body.replaceAll(CRLF, "");

            }
        }

        return body.trim();
    }


    public List<PATAttachmentDTO> fillAttach(){
        String data = emailRaw.getData().getBody();
        int startBodySplitIndex = data.indexOf("--");

        if (startBodySplitIndex == -1) {
            return null;
        }

        int endBodySplitIndex = data.substring(startBodySplitIndex).indexOf(CRLF) + startBodySplitIndex;
        String bodySplit = data.substring(startBodySplitIndex, endBodySplitIndex);
        String[] bodyList = data.split(bodySplit);
        List<PATAttachmentDTO> attachmentDTOList = new ArrayList<>();
        for(String b : bodyList){
            if (b.contains(TEXT_HTML) || b.contains(TEXT_PLAIN) || b.contains(END_BODY) || b.contains(START_BODY)){
                continue;
            }

            PATAttachmentDTO dto = new PATAttachmentDTO();
            dto.setVrAttachmentDocument(b);
            attachmentDTOList.add(dto);
        }

        return attachmentDTOList;
    }

}
