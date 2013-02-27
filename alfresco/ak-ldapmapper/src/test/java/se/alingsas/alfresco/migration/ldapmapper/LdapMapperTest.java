/*
 * Copyright (C) 2012-2013 Alingsås Kommun
 *
 * This file is part of Alfresco customizations made for Alingsås Kommun
 *
 * The Alfresco customizations made for Alingsås Kommun is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Alfresco customizations made for Alingsås Kommun is distributed in the 
 * hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the Alfresco customizations made for Alingsås Kommun. 
 * If not, see <http://www.gnu.org/licenses/>.
 */

package se.alingsas.alfresco.migration.ldapmapper;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ldap.core.LdapTemplate;

public class LdapMapperTest {

	/*@Test
	Disabled test due to dependecy to local ldap server 
	  public void testGetUser() {
		ApplicationContext context = new ClassPathXmlApplicationContext("ldap-context.xml");
		LdapMapper ldapMapper = new LdapMapper("", "");
		ldapMapper.setLdapTemplate(context.getBean("ldapTemplate", LdapTemplate.class));
		LotusUser user = ldapMapper
				.getUser("Eva Karlsson", "OU=KLK,O=Alingsas,DC=nodomain");
		assertNotNull(user);
		user = ldapMapper
				.getUser("Malin Wallin", "OU=KLK,O=Alingsas,DC=nodomain");
		assertNotNull(user);
	}*/

}
