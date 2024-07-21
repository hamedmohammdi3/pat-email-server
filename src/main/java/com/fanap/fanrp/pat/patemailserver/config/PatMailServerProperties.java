package com.fanap.fanrp.pat.patemailserver.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author H.Mohammadi
 * @created 2021/11/24
 */
@ConfigurationProperties(prefix = "mail-server-config")
public class PatMailServerProperties {

    private int smtpPort;
    private int imapPort;
    private int popPort;
    private int adminPort;
    private int smtpMsaPort;
    private String hostName;


    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public int getImapPort() {
        return imapPort;
    }

    public void setImapPort(int imapPort) {
        this.imapPort = imapPort;
    }

    public int getPopPort() {
        return popPort;
    }

    public void setPopPort(int popPort) {
        this.popPort = popPort;
    }

    public int getAdminPort() {
        return adminPort;
    }

    public void setAdminPort(int adminPort) {
        this.adminPort = adminPort;
    }

    public int getSmtpMsaPort() {
        return smtpMsaPort;
    }

    public void setSmtpMsaPort(int smtpMsaPort) {
        this.smtpMsaPort = smtpMsaPort;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}



