/*
 * Created on 21/Mar/2003
 */
package ServidorPersistente.OJB;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;

import Dominio.Gratuity;
import Dominio.IGratuity;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.IPersistentGratuity;
import Util.State;

/**
 * @author Nuno Nunes (nmsn@rnl.ist.utl.pt) Joana Mota (jccm@rnl.ist.utl.pt)
 */
public class GratuityOJB extends ObjectFenixOJB implements IPersistentGratuity
{

    public GratuityOJB()
    {
    }

    public List readByStudentCurricularPlanID(Integer studentCurricularPlanID)
            throws ExcepcaoPersistencia
    {
        Criteria crit = new Criteria();
        crit.addEqualTo("keyStudentCurricularPlan", studentCurricularPlanID);
        return queryList(Gratuity.class, crit);

    }

    public IGratuity readByStudentCurricularPlanIDAndState(Integer studentCurricularPlanID, State state)
            throws ExcepcaoPersistencia
    {
        Criteria crit = new Criteria();
        crit.addEqualTo("keyStudentCurricularPlan", studentCurricularPlanID);
        crit.addEqualTo("state", state);
        return (IGratuity) queryObject(Gratuity.class, crit);

    }

}