package com.fanap.fanrp.pat.patemailserver;

import com.fanap.fanrp.pat.patemailserver.config.PatMailServerProperties;
import com.fanap.fanrp.pat.patemailserver.core.admin.AdminConnectionListener;
import com.fanap.fanrp.pat.patemailserver.core.db.DbDao;
import com.fanap.fanrp.pat.patemailserver.core.db.DirbyEmbeddedDbDao;
import com.fanap.fanrp.pat.patemailserver.core.db.DirbyMemoryDbDao;
import com.fanap.fanrp.pat.patemailserver.imapserver.IMAPConnectionListener;
import com.fanap.fanrp.pat.patemailserver.pop3server.POP3ConnectionListener;
import com.fanap.fanrp.pat.patemailserver.service.RestApiService;
import com.fanap.fanrp.pat.patemailserver.smtpserver.MailSender;
import com.fanap.fanrp.pat.patemailserver.smtpserver.SMTPConnectionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Entry point into the application.
 *
 * @author Mike Angstadt [mike.angstadt@gmail.com]
 */
@Slf4j
@Component
public class Runner implements CommandLineRunner {


    @Autowired
    PatMailServerProperties patMailServerProperties;

    public static final String appName = "Pat-email-server";
    public static final String version = "0.1";


    public void runMainServer() throws Exception {

		/*Arguments arguments = new Arguments(args);
		if (arguments.exists(null, "help")) {
			System.out.println(appName + " v" + version);
			System.out.println("Pat-email-server is an SMTP server with support for the POP3 protocol.");
			System.out.println();
		}

		if (arguments.exists(null, "version")) {
			System.out.println(appName + " v" + version);
			System.exit(0);
		}

		//check for non-existant arguments
		Set<String> validArgs = new HashSet<String>(Arrays.asList(new String[] { "smtp-port", "smtp-msa-port", "pop3-port","imap-port", "admin-port", "host-name", "database", "smtp-inbound-log", "smtp-outbound-log", "smtp-msa-log", "pop3-log","imap-log", "admin-log", "version", "help" }));
		Collection<String> invalidArgs = arguments.invalidArgs(validArgs);
		if (!invalidArgs.isEmpty()) {
			System.err.println("One or more non-existent arguments were specified:\n" + invalidArgs);
			System.exit(1);
		}*/

        //host name is required
		/*if (hostName == null) {
			System.err.println("A host name must be specified.");
			System.exit(1);
		}*/

        final String hostName = patMailServerProperties.getHostName();
        final int smtpPort = patMailServerProperties.getSmtpPort();
        final int smtpMsaPort = patMailServerProperties.getSmtpMsaPort();
        final int popPort = patMailServerProperties.getPopPort();
        final int imapPort = patMailServerProperties.getImapPort();
        final int adminPort = patMailServerProperties.getAdminPort();

        String smtpInboundLog = "smtpInboundLog.log";
        String smtpOutboundLog = "smtpOutboundLog.log";
        String smtpMsaLog = "smtpMsaLog.log";
        String pop3Log = "pop3Log.log";
        String imapLog = "imapLog.log";
        String adminLog = "adminLog.db";

        //connect to the database
        String dbPath = ".";
        DbDao dao;
        if ("MEM".equals(dbPath)) {
            dao = new DirbyMemoryDbDao();
        } else {
            File databaseDir = new File(dbPath);
            dao = new DirbyEmbeddedDbDao(databaseDir);
        }

        //start the mail sender
        final MailSender mailSender = new MailSender(dao);
        mailSender.setHostName(hostName);
        if (smtpOutboundLog != null) {
            mailSender.setTransactionLogFile(new File(smtpOutboundLog));
        }
        Thread mailSenderThread = new Thread() {
            @Override
            public void run() {
                try {
                    mailSender.start();
                } catch (Exception e) {
                    log.error("An error occurred with the mail sender.  Mail sender terminated.", e);
                    throw new RuntimeException(e);
                }
            }
        };
        mailSenderThread.start();

        //start the POP server
        final POP3ConnectionListener popServer = new POP3ConnectionListener(dao);
        popServer.setHostName(hostName);
        popServer.setPort(popPort);
        if (pop3Log != null) {
            popServer.setTransactionLogFile(new File(pop3Log));
        }
        Thread popThread = new Thread() {
            @Override
            public void run() {
                try {
                    popServer.start();
                } catch (Exception e) {
                    log.error("The POP3 server encountered an error.  Server terminated.", e);
                    throw new RuntimeException("Cannot start POP3 server on port " + popPort + ".", e);
                }
            }
        };
        popThread.start();

        //start the IMAP server
        final IMAPConnectionListener imapServer = new IMAPConnectionListener(dao);
        imapServer.setHostName(hostName);
        imapServer.setPort(imapPort);
        if (imapLog != null) {
            imapServer.setTransactionLogFile(new File(imapLog));
        }
        Thread imapThread = new Thread() {
            @Override
            public void run() {
                try {
                    imapServer.start();
                } catch (Exception e) {
                    log.error("The IMAP server encountered an error.  Server terminated.", e);
                    throw new RuntimeException("Cannot start IMAP server on port " + popPort + ".", e);
                }
            }
        };
        imapThread.start();

        //start the SMTP server
        final SMTPConnectionListener smtpServer = new SMTPConnectionListener(dao);
        smtpServer.setHostName(hostName);
        smtpServer.setPort(smtpPort);
        if (smtpInboundLog != null) {
            smtpServer.setTransactionLogFile(new File(smtpInboundLog));
        }
        Thread smtpThread = new Thread() {
            @Override
            public void run() {
                try {
                    smtpServer.start();
                } catch (Exception e) {
                    log.error("The SMTP server encountered an error.  Server terminated.", e);
                    throw new RuntimeException("Cannot start SMTP server on port " + smtpPort + ".", e);
                }
            }
        };
        smtpThread.start();

        //start the SMTP MSA (mail submission agent) server
        final SMTPConnectionListener smtpMsaServer = new SMTPConnectionListener(dao, mailSender);
        smtpMsaServer.setHostName(hostName);
        smtpMsaServer.setPort(smtpMsaPort);
        if (smtpMsaLog != null) {
            smtpMsaServer.setTransactionLogFile(new File(smtpMsaLog));
        }
        Thread smtpMsaThread = new Thread() {
            @Override
            public void run() {
                try {
                    smtpMsaServer.start();
                } catch (Exception e) {
                    log.error("The SMTP mail submission server encountered an error.  Server terminated.", e);
                    throw new RuntimeException("Cannot start SMTP mail submission server on port " + smtpMsaPort + ".", e);
                }
            }
        };
        smtpMsaThread.start();

        //start the admin console
        final AdminConnectionListener adminServer = new AdminConnectionListener(dao);
        adminServer.setHostName(hostName);
        adminServer.setPort(adminPort);
        if (adminLog != null) {
            adminServer.setTransactionLogFile(new File(adminLog));
        }
        Thread adminThread = new Thread() {
            @Override
            public void run() {
                try {
                    adminServer.start();
                } catch (Exception e) {
                    log.error("The Pat-email-server admin console encountered an error.  Server terminated.", e);
                    throw new RuntimeException("Cannot start Pat-email-server admin console on port " + adminPort + ".", e);
                }
            }
        };
        adminThread.start();

        //start the REST API Service

        Thread restApiThread = new Thread() {
            @Override
            public void run() {
                try {
                    RestApiService.getInstance().runServer();
                } catch (Exception e) {
                    log.error("The Pat-email-server REST API Service encountered an error.  Server terminated.", e);
                    throw new RuntimeException("Cannot start Pat-email-server REST API Service on port " + adminPort + ".", e);
                }
            }
        };
        restApiThread.start();
    }

    @Override
    public void run(String... args) throws Exception {
        runMainServer();
    }
}
