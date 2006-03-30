/*
 * Created on 26/Ago/2003, 13:35:57
 *
 *By Goncalo Luiz gedl [AT] rnl [DOT] ist [DOT] utl [DOT] pt
 */
package net.sourceforge.fenixedu.applicationTier.Filtro.Seminaries;

import net.sourceforge.fenixedu.applicationTier.Filtro.Filtro;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.NotAuthorizedException;
import net.sourceforge.fenixedu.domain.Student;
import net.sourceforge.fenixedu.domain.Seminaries.Candidacy;
import pt.utl.ist.berserk.ServiceRequest;
import pt.utl.ist.berserk.ServiceResponse;

/**
 * @author Goncalo Luiz gedl [AT] rnl [DOT] ist [DOT] utl [DOT] pt
 * 
 * 
 * Created at 26/Ago/2003, 13:35:57
 * 
 */
public class CandidacyOwnershipFilter extends Filtro {
    public CandidacyOwnershipFilter() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.utl.ist.berserk.logic.filterManager.IFilter#execute(pt.utl.ist.berserk.ServiceRequest,
     *      pt.utl.ist.berserk.ServiceResponse)
     */
    public void execute(ServiceRequest request, ServiceResponse response) throws Exception {
        Integer candidacyID = (Integer) getServiceCallArguments(request)[0];

        Student student = Student.readByUsername(getRemoteUser(request).getUtilizador());
        Candidacy candidacy = rootDomainObject.readCandidacyByOID(candidacyID);
        //
        if ((candidacy != null)
                && (candidacy.getStudent().getIdInternal().intValue() != student.getIdInternal()
                        .intValue()))
            throw new NotAuthorizedException();

    }
}