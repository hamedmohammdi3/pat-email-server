package com.fanap.fanrp.pat.patemailserver.odata;

import com.fanap.fanrp.pat.patemailserver.core.db.POPEmail;
import com.fanap.fanrp.pat.patemailserver.utils.EmailConvertor;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ODataRequest {

    public static void loadAddressBook() throws IOException, ODataException {
        ODataService oDataService = new ODataService(null);
        ODataFeed dataFeed = oDataService.readFeed("PatEmailAddressBook");
        if (dataFeed != null && dataFeed.getEntries() != null && !dataFeed.getEntries().isEmpty()) {
            for (ODataEntry oDataEntry : dataFeed.getEntries()) {
                System.out.println(oDataEntry.getProperties().get("no"));
            }
        }else{
            System.out.println("empty");
        }
    }

    public static List<POPEmail> loadInboxByPerson(String filter) throws IOException, ODataException {
        ODataService oDataService = new ODataService(null);
        List<POPEmail> popEmailList = new ArrayList<>();
        ODataFeed dataFeed = oDataService.readFeedByFilter("PatEmailInbox", filter, "$top=200");
        if (dataFeed != null && dataFeed.getEntries() != null && !dataFeed.getEntries().isEmpty()) {
            EmailConvertor emailConvertor = new EmailConvertor();
            EmailConvertor.resultSetEmail.clear();
            EmailConvertor.resultSet.clear();
            for (ODataEntry oDataEntry : dataFeed.getEntries()) {
                POPEmail popEmail = emailConvertor.toMailRaw(oDataEntry.getProperties());
                popEmailList.add(popEmail);
            }
        }
        return popEmailList;
    }
}
