package com.custom.email;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.email.Connection;
import org.jbpm.process.workitem.email.Email;
import org.jbpm.process.workitem.email.EmailWorkItemHandler;
import org.jbpm.process.workitem.email.Message;
import org.jbpm.process.workitem.email.Recipient;
import org.jbpm.process.workitem.email.Recipients;
import org.jbpm.process.workitem.email.SendHtml;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class CustomEmailTemplateWorkItemHandler extends AbstractLogOrThrowWorkItemHandler{

	private Connection connection;
	
    public CustomEmailTemplateWorkItemHandler() {
	
	}
	
	public CustomEmailTemplateWorkItemHandler(String host, String port, String userName, String password) {
		setConnection(host, port, userName, password);
	}
	
	public CustomEmailTemplateWorkItemHandler(String host, String port, String userName, String password, String startTls) {
		setConnection(host, port, userName, password, startTls);
	}
	
	public void setConnection(String host, String port, String userName, String password) {
		connection = new Connection();
		connection.setHost(host);
		connection.setPort(port);
		connection.setUserName(userName);
		connection.setPassword(password);
	}
	
	public void setConnection(String host, String port, String userName, String password, String startTls) {
		connection = new Connection();
		connection.setHost(host);
		connection.setPort(port);
		connection.setUserName(userName);
		connection.setPassword(password);
		connection.setStartTls(Boolean.parseBoolean(startTls));
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		if (connection == null) {
			throw new IllegalArgumentException("Connection not initialized for Email");
		}
		try {
    		Email email = createEmail(workItem, connection);
    		SendHtml.sendHtml(email, getDebugFlag(workItem));
    		// avoid null pointer when used from deadline escalation handler
    	    if (manager != null) {
    	 	  manager.completeWorkItem(workItem.getId(), null);    	 	
    	    }
		} catch (Exception e) {
		    handleException(e);
		}
	}

	protected static Email createEmail(WorkItem workItem, Connection connection) { 
	    Email email = new Email();
        Message message = new Message();
        message.setFrom((String) workItem.getParameter("From"));
        message.setReplyTo( (String) workItem.getParameter("Reply-To"));
        
        // Set recipients
        Recipients recipients = new Recipients();
        String to = (String) workItem.getParameter("To");
        if ( to == null || to.trim().length() == 0 ) {
            throw new RuntimeException( "Email must have one or more to adresses" );
        }
        for (String s: to.split(";")) {
            if (s != null && !"".equals(s)) {
                Recipient recipient = new Recipient();
                recipient.setEmail(s);
                recipient.setType( "To" );
                recipients.addRecipient(recipient);
            }
        }
        
        // Set cc
        String cc = (String) workItem.getParameter("Cc");
        if ( cc != null && cc.trim().length() > 0 ) {
            for (String s: cc.split(";")) {
                if (s != null && !"".equals(s)) {
                    Recipient recipient = new Recipient();
                    recipient.setEmail(s);
                    recipient.setType( "Cc" );
                    recipients.addRecipient(recipient);
                }
            }       
        }
        
        // Set bcc
        String bcc = (String) workItem.getParameter("Bcc");
        if ( bcc != null && bcc.trim().length() > 0 ) {
            for (String s: bcc.split(";")) {
                if (s != null && !"".equals(s)) {
                    Recipient recipient = new Recipient();
                    recipient.setEmail(s);
                    recipient.setType( "Bcc" );
                    recipients.addRecipient(recipient);
                }
            }       
        }
        
        /**
         * Customization
         */
		String body = CustomEmailTemplateGenerator.templateizeEmail(workItem.getParameters(), (String)workItem.getParameter("templateName"));

        // Fill message
        message.setRecipients(recipients);
        message.setSubject((String) workItem.getParameter("Subject"));
        message.setBody(body);
        
        // fill attachments
        String attachmentList = (String) workItem.getParameter("Attachments");
        if (attachmentList != null) {
            String[] attachments = attachmentList.split(",");
            message.setAttachments(Arrays.asList(attachments));
        }
        
        // setup email
        email.setMessage(message);
        email.setConnection(connection);
        
        return email;
	}
	
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// Do nothing, email cannot be aborted
	}
	
	protected boolean getDebugFlag(WorkItem workItem) {
	    Object debugParam  = workItem.getParameter("Debug");
	    if (debugParam == null) {
	        return false;
	    }
	    
	    return Boolean.parseBoolean(debugParam.toString());
	}

}
