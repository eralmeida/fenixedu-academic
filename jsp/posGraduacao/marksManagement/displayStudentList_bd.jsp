<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="org.apache.struts.action.Action" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="Util.Data" %>
<%@ page import="ServidorApresentacao.Action.sop.utils.SessionConstants" %>
<%@ page import="Util.EnrolmentState" %>



<logic:present name="jspTitle">
	<h2><bean:write name="jspTitle" /></h2>
	<br />
</logic:present>

   <span class="error"><html:errors/></span>
	<br />

  <bean:define id="studentList" name="studentList" scope="request" />

  <bean:define id="link">/changeMarkDispatchAction.do?method=chooseStudentMarks<%= "&" %>page=0<%= "&showMarks=showMarks" %> <%= "&executionYear=" + pageContext.findAttribute("executionYear") + "&degree=" + pageContext.findAttribute("degree") + "&curricularCourse=" + pageContext.findAttribute("curricularCourse") + "&jspTitle=" + pageContext.findAttribute("jspTitle") + "&curricularCourseCode=" + pageContext.findAttribute("curricularCourseCode") %><%= "&" %>studentNumber=</bean:define>
  <p>
    <h3><%= ((List) studentList).size()%> <bean:message key="label.masterDegree.administrativeOffice.studentsFound"/></h3>        
    <% if (((List) studentList).size() != 0) { %>
        </p>
        <bean:message key="label.masterDegree.chooseOne"/><br><br><br>
    
        <table>
        	<tr>
    			<td class="listClasses-header"><bean:message key="label.candidate.number" /></td>
    			<td class="listClasses-header"><bean:message key="label.person.name" /></td>
    			<td class="listClasses-header"><bean:message key="label.mark" /></td>
    			<td class="listClasses-header"><bean:message key="label.examDate" /></td>
    			<td class="listClasses-header"><bean:message key="label.gradeAvailableDate" /></td>
    			<td class="listClasses-header"><bean:message key="label.enrolmentEvaluationType" /></td>
    			<td class="listClasses-header"><bean:message key="label.teacherName"  /></td>
    			<td class="listClasses-header"><bean:message key="label.employee"  /></td>
    			<td class="listClasses-header"><bean:message key="label.when"  /></td>
    			<td class="listClasses-header"><bean:message key="label.observation"  /></td>
    			
    		</tr>
     	<logic:iterate id="enrolment" name="studentList">
        		<bean:define id="studentLink">
        		<bean:write name="link"/><bean:write name="enrolment" property="infoStudentCurricularPlan.infoStudent.number"/>
        	</bean:define>
        		
        	
        <tr>
        	<td class="listClasses">
      		<html:link page='<%= pageContext.findAttribute("studentLink").toString() %>'>	
      		<bean:write name="enrolment" property="infoStudentCurricularPlan.infoStudent.number"/>
    		</html:link>
       <%-- 		<bean:write name="enrolment" property="infoStudentCurricularPlan.infoStudent.number"/> --%>
            </td>
            <td class="listClasses">
    	        <bean:write name="enrolment" property="infoStudentCurricularPlan.infoStudent.infoPerson.nome"/>
    	    </td>
            <td class="listClasses">
  				<bean:write name="enrolment" property="infoEnrolmentEvaluation.grade"/>			
    	    </td>
    	    <td class="listClasses">
    	    	<bean:define id="date" name="enrolment" property="infoEnrolmentEvaluation.examDate" />
				<%= Data.format2DayMonthYear((Date) date) %>		
    	    </td>
    	    <td class="listClasses">
    	    	<bean:define id="date" name="enrolment" property="infoEnrolmentEvaluation.gradeAvailableDate" />
				<%= Data.format2DayMonthYear((Date) date) %>					
    	    </td>
    	    <td  class="listClasses" >
				<bean:write name="enrolment" property="infoEnrolmentEvaluation.enrolmentEvaluationType" />
			</td>
			<td  class="listClasses" >
				<bean:write name="enrolment" property="infoEnrolmentEvaluation.infoPersonResponsibleForGrade.nome" />
			</td>
			<logic:empty name="enrolment" property="infoEnrolmentEvaluation.infoEmployee" >	
				<td  class="listClasses" >
					
				</td> 
				<td  class="listClasses" >
					
				</td> 
			</logic:empty>
			<logic:notEmpty name="enrolment" property="infoEnrolmentEvaluation.infoEmployee" >	
				<td  class="listClasses" >
					<bean:write name="enrolment" property="infoEnrolmentEvaluation.infoEmployee.nome"/>	
				</td> 
				<td  class="listClasses" >
					<bean:define id="date" name="enrolment" property="infoEnrolmentEvaluation.when" />
						<%= Data.format2DayMonthYear((Date) date) %>	
				</td> 
			</logic:notEmpty>
			<td  class="listClasses" >
				<bean:write name="enrolment" property="infoEnrolmentEvaluation.observation"/>	
			</td>	
    	</tr>
        </logic:iterate>
      	</table>    	
   	<% } %>  
	<html:hidden property="executionYear" value="<%= pageContext.findAttribute("executionYear").toString() %>" />
	<html:hidden property="degree" value="<%= pageContext.findAttribute("degree").toString() %>" />
	<html:hidden property="curricularCourse" value="<%= pageContext.findAttribute("curricularCourse").toString() %>" />
	<html:hidden property="curricularCourseCode" value="<%= pageContext.findAttribute("curricularCourseCode").toString() %>" />
	<html:hidden property="scopeCode" value="<%= pageContext.findAttribute("curricularCourseCode").toString() %>" />
	<html:hidden property="jspTitle=" value="<%= pageContext.findAttribute("jspTitle").toString() %>" />
	<logic:present name="showMarks">
		<html:hidden property="showMarks=" value="showMarks"/>
	</logic:present>
	
	<html:hidden property="page" value="2"/>