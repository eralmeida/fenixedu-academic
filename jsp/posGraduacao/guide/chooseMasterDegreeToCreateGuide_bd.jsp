<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="org.apache.struts.action.Action" %>
<%@ page import="java.util.List" %>
<%@ page import="ServidorApresentacao.Action.sop.utils.SessionConstants" %>

<span class="error"><html:errors/></span>

<bean:define id="link">/chooseDataToCreateGuide.do?method=chooseMasterDegreeCurricularPlanFromList<%= "&" %>page=0<%= "&" %>degreeID=</bean:define>

<logic:present name="<%= SessionConstants.MASTER_DEGREE_LIST %>" scope="request">
	<bean:define id="masterDegreeList" name="<%= SessionConstants.MASTER_DEGREE_LIST %>" scope="request" />
	<h3><%= ((List) masterDegreeList).size()%> <bean:message key="label.masterDegree.administrativeOffice.degreesFound"/></h3>        
	<bean:message key="label.masterDegree.chooseOne"/><br><br>
	<bean:message key="label.manager.degrees" /><br>
 	<logic:iterate id="masterDegree" name="masterDegreeList">
    	<bean:define id="masterDegreeLink">
    		<bean:write name="link"/><bean:write name="masterDegree" property="idInternal"/>
    	</bean:define>
    	<html:link page='<%= pageContext.findAttribute("masterDegreeLink").toString() %>'>
			<bean:write name="masterDegree" property="nome"/> - 
			<bean:write name="masterDegree" property="sigla"/>
			<br>
       	</html:link>
	</logic:iterate>
</logic:present>
  	
<logic:notPresent name="<%= SessionConstants.MASTER_DEGREE_LIST %>" scope="request">
	<h3>0&nbsp;<bean:message key="label.masterDegree.administrativeOffice.degreesFound"/></h3>        
</logic:notPresent>