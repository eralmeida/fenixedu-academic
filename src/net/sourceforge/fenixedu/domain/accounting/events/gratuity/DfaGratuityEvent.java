package net.sourceforge.fenixedu.domain.accounting.events.gratuity;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sourceforge.fenixedu.dataTransferObject.accounting.EntryDTO;
import net.sourceforge.fenixedu.dataTransferObject.accounting.SibsTransactionDetailDTO;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.StudentCurricularPlan;
import net.sourceforge.fenixedu.domain.User;
import net.sourceforge.fenixedu.domain.accounting.Entry;
import net.sourceforge.fenixedu.domain.accounting.EntryType;
import net.sourceforge.fenixedu.domain.accounting.EventState;
import net.sourceforge.fenixedu.domain.accounting.PaymentCodeState;
import net.sourceforge.fenixedu.domain.accounting.PaymentCodeType;
import net.sourceforge.fenixedu.domain.accounting.paymentCodes.AccountingEventPaymentCode;
import net.sourceforge.fenixedu.domain.administrativeOffice.AdministrativeOffice;
import net.sourceforge.fenixedu.domain.degree.DegreeType;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.student.Student;
import net.sourceforge.fenixedu.util.Money;

import org.joda.time.DateTime;
import org.joda.time.YearMonthDay;

public class DfaGratuityEvent extends DfaGratuityEvent_Base {

    protected DfaGratuityEvent() {
	super();
    }

    public DfaGratuityEvent(AdministrativeOffice administrativeOffice, Person person,
	    StudentCurricularPlan studentCurricularPlan, ExecutionYear executionYear) {

	this();

	checkRulesToCreate(studentCurricularPlan);
	init(administrativeOffice, person, studentCurricularPlan, executionYear);
    }

    private void checkRulesToCreate(StudentCurricularPlan studentCurricularPlan) {
	if (studentCurricularPlan.getDegreeType() != DegreeType.BOLONHA_ADVANCED_FORMATION_DIPLOMA) {
	    throw new DomainException(
		    "error.net.sourceforge.fenixedu.domain.accounting.events.gratuity.DfaGratuityEvent.invalid.degreeType");
	}
    }

    @Override
    public boolean canApplyExemption() {
	return !isCustomEnrolmentModel();
    }

    @Override
    protected List<AccountingEventPaymentCode> updatePaymentCodes() {
	final EntryDTO entryDTO = calculateEntries(new DateTime()).get(0);

	if (!getNonProcessedPaymentCodes().isEmpty()) {
	    getNonProcessedPaymentCodes().get(0).update(new YearMonthDay(),
		    calculatePaymentCodeEndDate(), entryDTO.getAmountToPay(), entryDTO.getAmountToPay());
	}

	return getNonProcessedPaymentCodes();

    }

    @Override
    protected List<AccountingEventPaymentCode> createPaymentCodes() {
	final EntryDTO entryDTO = calculateEntries(new DateTime()).get(0);

	return Collections.singletonList(AccountingEventPaymentCode.create(
		PaymentCodeType.TOTAL_GRATUITY, new YearMonthDay(), calculatePaymentCodeEndDate(), this,
		entryDTO.getAmountToPay(), entryDTO.getAmountToPay(), getStudent()));
    }

    private Student getStudent() {
	return getStudentCurricularPlan().getRegistration().getStudent();
    }

    private YearMonthDay calculatePaymentCodeEndDate() {
	return calculateNextEndDate(new YearMonthDay());
    }

    @Override
    public boolean isExemptionAppliable() {
	return true;
    }

    @Override
    protected Set<Entry> internalProcess(User responsibleUser, AccountingEventPaymentCode paymentCode,
	    Money amountToPay, SibsTransactionDetailDTO transactionDetail) {
	return internalProcess(responsibleUser, Collections.singletonList(new EntryDTO(
		EntryType.GRATUITY_FEE, this, amountToPay)), transactionDetail);
    }

    @Override
    public boolean isOpen() {
	if (isCancelled()) {
	    return false;
	}

	return calculateAmountToPay(new DateTime()).greaterThan(Money.ZERO);
    }

    @Override
    public boolean isClosed() {
	if (isCancelled()) {
	    return false;
	}

	return calculateAmountToPay(new DateTime()).lessOrEqualThan(Money.ZERO);
    }

    @Override
    public boolean isInState(final EventState eventState) {
	if (eventState == EventState.OPEN) {
	    return isOpen();
	} else if (eventState == EventState.CLOSED) {
	    return isClosed();
	} else if (eventState == EventState.CANCELLED) {
	    return isCancelled();
	} else {
	    throw new DomainException(
		    "error.net.sourceforge.fenixedu.domain.accounting.events.gratuity.DfaGratuityEvent.unexpected.state.to.test");
	}
    }

    @Override
    protected void internalRecalculateState(DateTime whenRegistered) {
	// We can safely change event state and date because the are no
	// penalties
	if (canCloseEvent(whenRegistered)) {
	    closeNonProcessedCodes();
	    closeEvent();
	} else {
	    if (getCurrentEventState() != EventState.OPEN) {
		changeState(EventState.OPEN, new DateTime());
		reopenCancelledCodes();
	    }
	}
    }

    // TODO: Perhaps this method should be in superclass and each subclass
    // should reimplement
    // logic to reopen payment codes
    private void reopenCancelledCodes() {
	for (final AccountingEventPaymentCode paymentCode : getCancelledPaymentCodes()) {
	    paymentCode.setState(PaymentCodeState.NEW);
	}
    }

    @Override
    public boolean isOtherPartiesPaymentsSupported() {
	return true;
    }

}
