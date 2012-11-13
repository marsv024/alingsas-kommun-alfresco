package se.alingsas.alfresco.migration.ldapmapper;

import java.io.File;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ldap.core.LdapTemplate;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length<2) {
			System.out.println("Error: Invalid arguments");
			System.out.println("Usage: java -cp ldapmapper-1.0.0.jar se.alingsas.alfresco.migration.ldapmapper.Main sourcefile targetfile");
			System.out.println("Info: A file called ldap.properties must exist on the classpath which contains connection details for the ldap server");
		} else {
			String sourceFile = args[0];
			String destinationFile = args[1];

			
			File file=new File(sourceFile);
			if (!file.exists()) {
				System.out.println("Error: Cound not find source file "+ sourceFile);
				return;
			}
			
			file=new File(destinationFile);
			if (file.exists()) {
				System.out.println("Error: Destination file already exists "+ destinationFile);
				return;
			}
			
			ApplicationContext context = new ClassPathXmlApplicationContext("ldap-context.xml");
			
			
			LdapMapper ldapMapper = new LdapMapper(sourceFile, destinationFile);
			ldapMapper.setLdapTemplate(context.getBean("ldapTemplate", LdapTemplate.class));

			ldapMapper.run();
		}
		
	}

}
