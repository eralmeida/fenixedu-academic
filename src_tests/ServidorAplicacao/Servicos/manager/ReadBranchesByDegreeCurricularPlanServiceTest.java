/*
 * Created on 2/Set/2003
 */
package ServidorAplicacao.Servicos.manager;


/**
 * @author lmac1
 */

public class ReadBranchesByDegreeCurricularPlanServiceTest extends TestCaseManagerReadServices {
	    
	/**
	 * @param testName
	 */
	 public ReadBranchesByDegreeCurricularPlanServiceTest(String testName) {
		super(testName);
	 }

	 protected String getNameOfServiceToBeTested() {
		return "ReadBranchesByDegreeCurricularPlan";
	 }
		
	protected Object[] getArgumentsOfServiceToBeTestedSuccessfuly() {
		Object[] args = { new Integer(1) };
		return args;
	}
		
	protected int getNumberOfItemsToRetrieve() {
		return 3;
	}
}