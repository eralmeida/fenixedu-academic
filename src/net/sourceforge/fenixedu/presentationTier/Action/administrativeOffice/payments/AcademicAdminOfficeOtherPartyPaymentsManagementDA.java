package net.sourceforge.fenixedu.presentationTier.Action.administrativeOffice.payments;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.fenixedu.domain.administrativeOffice.AdministrativeOffice;
import net.sourceforge.fenixedu.presentationTier.Action.commons.administrativeOffice.payments.OtherPartyPaymentManagementDA;

public class AcademicAdminOfficeOtherPartyPaymentsManagementDA extends OtherPartyPaymentManagementDA {

    @Override
    protected AdministrativeOffice getAdministrativeOffice(HttpServletRequest request) {
	return getUserView(request).getPerson().getEmployee().getAdministrativeOffice();
    }

}
