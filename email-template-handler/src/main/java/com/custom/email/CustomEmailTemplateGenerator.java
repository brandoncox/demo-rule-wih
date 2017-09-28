package com.custom.email;

import java.util.Iterator;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;


public class CustomEmailTemplateGenerator {

	public static String templateizeEmail(WorkItem workItem, String templateName) {

//		Properties props = new Properties();
//		props.setProperty(RuntimeConstants.RESOURCE_LOADER, "file"); 
//		props.setProperty("classpath.resource.loader.class", FileResourceLoader.class.getName());
//		props.setProperty("file.resource.loader.path", "/tmp");
//		props.setProperty("file.resource.loader.cache","true");
//		props.setProperty("file.resource.loader.modificationCheckInterval", "2");
//	    VelocityEngine velocityEngine = new VelocityEngine(props);
//		velocityEngine.init(props);
//		
//		
//		Template t = velocityEngine.getTemplate(templateName);
//		VelocityContext context = new VelocityContext();
//		context.put("input", input);
//		StringWriter writer = new StringWriter();
//		t.merge( context, writer );
//		return writer.toString();
		
		Map<String, Object> parameters = workItem.getParameters();
		
		StringBuilder builder = new StringBuilder();
		builder.append("<html><body><table>");
		Iterator it = parameters.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        builder.append("<tr><td>" + pair.getKey() + "</td><td>" + pair.getValue() +" </td><tr>");
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    
	    builder.append("<tr><td>ProcessID</td><td>" + workItem.getProcessInstanceId() + "</td></tr>");
	    builder.append("<tr><td>WorkItemID</td><td>" + workItem.getId() + "</td></tr>");
	    builder.append("<tr><td>WorkItemName</td><td>" + workItem.getName() + "</td></tr>");
	    builder.append("<tr><td>TemplateName</td><td>" + templateName + "</td></tr>");
		
	    
	    builder.append("</table></body></html>");
		return "<html><body>" + builder.toString() + "</body></html>";
	}

}
