
/**
 *
 * Autores :
 *   - Nuno Nunes (nmsn@rnl.ist.utl.pt)
 *   - Joana Mota (jccm@rnl.ist.utl.pt)
 *
 */

package ServidorAplicacao.Servico.masterDegree.administrativeOffice.marksManagement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;

import DataBeans.InfoEnrolment;
import DataBeans.InfoEnrolmentEvaluation;
import DataBeans.util.Cloner;
import Dominio.CurricularCourse;
import Dominio.ICurricularCourse;
import Dominio.IEnrolment;
import ServidorAplicacao.GestorServicos;
import ServidorAplicacao.IServico;
import ServidorAplicacao.IUserView;
import ServidorAplicacao.Servico.ExcepcaoInexistente;
import ServidorAplicacao.Servico.exceptions.FenixServiceException;
import ServidorAplicacao.Servico.exceptions.NonExistingServiceException;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;

public class ReadStudentMarksListByCurricularCourse implements IServico {
    
    private static ReadStudentMarksListByCurricularCourse servico = new ReadStudentMarksListByCurricularCourse();
    
    /**
     * The singleton access method of this class.
     **/
    public static ReadStudentMarksListByCurricularCourse getService() {
        return servico;
    }
    
    /**
     * The actor of this class.
     **/
    private ReadStudentMarksListByCurricularCourse() { 
    }
    
    /**
     * Returns The Service Name */
    
    public final String getNome() {
        return "ReadStudentMarksListByCurricularCourse";
    }
    
    
    public List run(IUserView userView, Integer curricularCourseID, String executionYear) throws ExcepcaoInexistente, FenixServiceException {

        ISuportePersistente sp = null;
        
        List enrolmentList = null;
         
        ICurricularCourse curricularCourse = null;
        try {
            sp = SuportePersistenteOJB.getInstance();
            
            // Read the Students
            
            ICurricularCourse curricularCourseTemp = new CurricularCourse();
            curricularCourseTemp.setIdInternal(curricularCourseID);
            curricularCourse = (ICurricularCourse) sp.getIPersistentCurricularCourse().readByOId(curricularCourseTemp, false);

			enrolmentList = sp.getIPersistentEnrolment().readByCurricularCourse(curricularCourse, executionYear);

        } catch (ExcepcaoPersistencia ex) {
            FenixServiceException newEx = new FenixServiceException("Persistence layer error");
            newEx.fillInStackTrace();
            throw newEx;
        } 


		if ((enrolmentList == null) || (enrolmentList.size() == 0)){
			throw new NonExistingServiceException();
		}
		
		return cleanList(enrolmentList, userView);		
    }

	/**
	 * @param studentCurricularPlans
	 * @return A list of Student curricular Plans without the duplicates
	 */
	private List cleanList(List studentCurricularPlans, IUserView userView) throws FenixServiceException {
		List result = new ArrayList();
		Integer numberAux = null;

		GestorServicos serviceManager = GestorServicos.manager();

		BeanComparator numberComparator = new BeanComparator("studentCurricularPlan.student.number");
		Collections.sort(studentCurricularPlans, numberComparator);


		Iterator iterator = studentCurricularPlans.iterator();
		while (iterator.hasNext()) {
			IEnrolment enrolment = (IEnrolment) iterator.next();

			if ((numberAux == null)
				|| (numberAux.intValue() != enrolment.getStudentCurricularPlan().getStudent().getNumber().intValue())) {
				numberAux = enrolment.getStudentCurricularPlan().getStudent().getNumber();
				
				Object args[] = {userView,enrolment };
				InfoEnrolmentEvaluation infoEnrolmentEvaluation =(InfoEnrolmentEvaluation) serviceManager.executar(userView, "GetEnrolmentMark", args);
				if (infoEnrolmentEvaluation != null){	
					InfoEnrolment infoEnrolment = Cloner.copyIEnrolment2InfoEnrolment(enrolment);
					infoEnrolment.setInfoEnrolmentEvaluation(infoEnrolmentEvaluation);
					result.add(infoEnrolment);
				}
			}
		}

		return result;
	}
	

}