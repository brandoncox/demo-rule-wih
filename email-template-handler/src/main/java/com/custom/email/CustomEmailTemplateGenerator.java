package com.custom.email;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

public class CustomEmailTemplateGenerator {

	public static String templateizeEmail(Map<String, Object> input, String templateName) {

		Properties props = new Properties();
		props.setProperty(RuntimeConstants.RESOURCE_LOADER, "file"); 
		props.setProperty("classpath.resource.loader.class", FileResourceLoader.class.getName());
		props.setProperty("file.resource.loader.path", "/tmp");
		props.setProperty("file.resource.loader.cache","true");
		props.setProperty("file.resource.loader.modificationCheckInterval", "2");
	    VelocityEngine velocityEngine = new VelocityEngine(props);
		velocityEngine.init(props);
		
		
		Template t = velocityEngine.getTemplate(templateName);
		VelocityContext context = new VelocityContext();
		context.put("input", input);
		StringWriter writer = new StringWriter();
		t.merge( context, writer );
		return writer.toString();
	}

}
