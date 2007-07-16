package net.sourceforge.fenixedu.domain.student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.fenixedu.dataTransferObject.student.StudentStatuteBean;
import net.sourceforge.fenixedu.domain.Attends;
import net.sourceforge.fenixedu.domain.DegreeCurricularPlan;
import net.sourceforge.fenixedu.domain.Enrolment;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.ExecutionPeriod;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.RootDomainObject;
import net.sourceforge.fenixedu.domain.StudentCurricularPlan;
import net.sourceforge.fenixedu.domain.accounting.PaymentCode;
import net.sourceforge.fenixedu.domain.accounting.PaymentCodeType;
import net.sourceforge.fenixedu.domain.accounting.paymentCodes.MasterDegreeInsurancePaymentCode;
import net.sourceforge.fenixedu.domain.administrativeOffice.AdministrativeOffice;
import net.sourceforge.fenixedu.domain.candidacy.Ingression;
import net.sourceforge.fenixedu.domain.degree.DegreeType;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.inquiries.InquiriesStudentExecutionPeriod;
import net.sourceforge.fenixedu.domain.onlineTests.DistributedTest;
import net.sourceforge.fenixedu.domain.onlineTests.StudentTestQuestion;
import net.sourceforge.fenixedu.domain.organizationalStructure.Unit;
import net.sourceforge.fenixedu.domain.student.registrationStates.RegistrationStateType;
import net.sourceforge.fenixedu.injectionCode.AccessControl;
import net.sourceforge.fenixedu.injectionCode.Checked;
import net.sourceforge.fenixedu.util.Money;
import net.sourceforge.fenixedu.util.PeriodState;
import net.sourceforge.fenixedu.util.StudentPersonalDataAuthorizationChoice;

import org.apache.commons.beanutils.BeanComparator;
import org.joda.time.YearMonthDay;

public class Student extends Student_Base {

    public final static Comparator<Student> NUMBER_COMPARATOR = new BeanComparator("number");

    public Student(Person person, Integer number) {
	super();
	setPerson(person);
	if (number == null || readStudentByNumber(number) != null) {
	    number = Student.generateStudentNumber();
	}
	setNumber(number);
	setRootDomainObject(RootDomainObject.getInstance());
    }

    public Student(Person person) {
	this(person, null);
    }

    public static Student readStudentByNumber(Integer studentNumber) {
	for (Student student : RootDomainObject.getInstance().getStudents()) {
	    if (student.getNumber().equals(studentNumber)) {
		return student;
	    }
	}
	return null;
    }

    public Collection<Registration> getRegistrationsByDegreeType(DegreeType degreeType) {
	List<Registration> result = new ArrayList<Registration>();
	for (Registration registration : getRegistrations()) {
	    if (registration.getDegreeType().equals(degreeType)) {
		result.add(registration);
	    }
	}
	return result;
    }

    public Collection<Registration> getRegistrationsByDegreeTypeAndExecutionPeriod(
	    DegreeType degreeType, ExecutionPeriod executionPeriod) {
	List<Registration> result = new ArrayList<Registration>();
	for (Registration registration : getRegistrations()) {
	    if (registration.getDegreeType().equals(degreeType)
		    && registration.hasStudentCurricularPlanInExecutionPeriod(executionPeriod)) {
		result.add(registration);
	    }
	}
	return result;
    }

    public Collection<Registration> getRegistrationsByDegreeTypes(DegreeType... degreeTypes) {
	List<DegreeType> degreeTypesList = Arrays.asList(degreeTypes);
	List<Registration> result = new ArrayList<Registration>();
	for (Registration registration : getRegistrations()) {
	    if (degreeTypesList.contains(registration.getDegreeType())) {
		result.add(registration);
	    }
	}
	return result;
    }

    @Deprecated
    public Registration getActiveRegistrationByDegreeType(DegreeType degreeType) {
	for (Registration registration : getRegistrations()) {
	    if (registration.getDegreeType().equals(degreeType) && registration.isActive()) {
		return registration;
	    }
	}
	return null;
    }

    public List<Registration> getActiveRegistrations() {
	final List<Registration> result = new ArrayList<Registration>();
	for (final Registration registration : getRegistrationsSet()) {
	    if (registration.isActive()) {
		result.add(registration);
	    }
	}
	return result;
    }

    public Registration getLastActiveRegistration() {
	List<Registration> activeRegistrations = getActiveRegistrations();
	return activeRegistrations.isEmpty() ? null : (Registration) Collections.max(
		activeRegistrations, new BeanComparator("startDate"));
    }

    public boolean hasActiveRegistrationForDegreeType(final DegreeType degreeType,
	    final ExecutionYear executionYear) {
	for (final Registration registration : getRegistrations()) {
	    if (registration.hasAnyEnrolmentsIn(executionYear)
		    && registration.getDegreeType() == degreeType) {
		return true;
	    }
	}
	return false;
    }

    public boolean hasAnyRegistrationInState(final RegistrationStateType stateType) {
	for (final Registration registration : getRegistrations()) {
	    if (registration.getActiveStateType() == stateType) {
		return true;
	    }
	}
	return false;
    }

    public static Integer generateStudentNumber() {
	Integer nextNumber = 0;
	for (Student student : RootDomainObject.getInstance().getStudents()) {
	    if (student.getNumber() < 100000 && student.getNumber() > nextNumber) {
		nextNumber = student.getNumber();
	    }
	}
	return nextNumber + 1;
    }

    public StudentDataByExecutionYear getActualExecutionYearStudentData() {
	for (StudentDataByExecutionYear studentData : getStudentDataByExecutionYear()) {
	    if (studentData.getExecutionYear().isCurrent()) {
		return studentData;
	    }
	}
	return null;
    }

    public StudentDataByExecutionYear getStudentDataByExecutionYear(ExecutionYear executionYear) {
	for (StudentDataByExecutionYear studentData : getStudentDataByExecutionYear()) {
	    if (studentData.getExecutionYear().equals(executionYear)) {
		return studentData;
	    }
	}
	return null;
    }

    public ResidenceCandidacies getResidenceCandidacyForCurrentExecutionYear() {
	if (getActualExecutionYearStudentData() == null) {
	    return null;
	}
	return getActualExecutionYearStudentData().getResidenceCandidacy();
    }

    public void setResidenceCandidacyForCurrentExecutionYear(String observations) {
	createCurrentYearStudentData();
	getActualExecutionYearStudentData()
		.setResidenceCandidacy(new ResidenceCandidacies(observations));
    }

    public void setResidenceCandidacy(ResidenceCandidacies residenceCandidacy) {
	ExecutionYear executionYear = ExecutionYear.getExecutionYearByDate(residenceCandidacy
		.getCreationDateDateTime().toYearMonthDay());
	StudentDataByExecutionYear studentData = getStudentDataByExecutionYear(executionYear);
	if (studentData == null) {
	    studentData = createStudentDataForExecutionYear(executionYear);
	}
	studentData.setResidenceCandidacy(residenceCandidacy);
    }

    public boolean getWorkingStudentForCurrentExecutionYear() {
	if (getActualExecutionYearStudentData() == null) {
	    return false;
	}
	return getActualExecutionYearStudentData().getWorkingStudent();
    }

    public void setWorkingStudentForCurrentExecutionYear() {
	createCurrentYearStudentData();
	getActualExecutionYearStudentData().setWorkingStudent(true);
    }

    public StudentPersonalDataAuthorizationChoice getPersonalDataAuthorizationForCurrentExecutionYear() {
	if (getActualExecutionYearStudentData() == null) {
	    return null;
	}
	return getActualExecutionYearStudentData().getPersonalDataAuthorization();
    }

    public boolean hasPersonDataAuthorizationChoiseForCurrentExecutionYear() {
	return getPersonalDataAuthorizationForCurrentExecutionYear() != null;
    }

    public void setPersonalDataAuthorizationForCurrentExecutionYear(
	    StudentPersonalDataAuthorizationChoice personalDataAuthorization) {
	createCurrentYearStudentData();
	getActualExecutionYearStudentData().setPersonalDataAuthorization(personalDataAuthorization);
    }

    public void setPersonalDataAuthorizationForExecutionYear(
	    StudentPersonalDataAuthorizationChoice personalDataAuthorization, ExecutionYear executionYear) {
	StudentDataByExecutionYear studentData = getStudentDataByExecutionYear(executionYear);
	if (studentData == null) {
	    studentData = createStudentDataForExecutionYear(executionYear);
	}
	studentData.setPersonalDataAuthorization(personalDataAuthorization);
    }

    private void createCurrentYearStudentData() {
	if (getActualExecutionYearStudentData() == null) {
	    new StudentDataByExecutionYear(this);
	}
    }

    private StudentDataByExecutionYear createStudentDataForExecutionYear(ExecutionYear executionYear) {
	if (getStudentDataByExecutionYear(executionYear) == null) {
	    return new StudentDataByExecutionYear(this, executionYear);
	}
	return getStudentDataByExecutionYear(executionYear);
    }

    public DegreeType getMostSignificantDegreeType() {
	if (isStudentOfDegreeType(DegreeType.MASTER_DEGREE))
	    return DegreeType.MASTER_DEGREE;
	if (isStudentOfDegreeType(DegreeType.DEGREE))
	    return DegreeType.DEGREE;
	if (isStudentOfDegreeType(DegreeType.BOLONHA_SPECIALIZATION_DEGREE))
	    return DegreeType.BOLONHA_SPECIALIZATION_DEGREE;
	if (isStudentOfDegreeType(DegreeType.BOLONHA_ADVANCED_FORMATION_DIPLOMA))
	    return DegreeType.BOLONHA_ADVANCED_FORMATION_DIPLOMA;
	if (isStudentOfDegreeType(DegreeType.BOLONHA_PHD_PROGRAM))
	    return DegreeType.BOLONHA_PHD_PROGRAM;
	if (isStudentOfDegreeType(DegreeType.BOLONHA_MASTER_DEGREE))
	    return DegreeType.BOLONHA_MASTER_DEGREE;
	if (isStudentOfDegreeType(DegreeType.BOLONHA_INTEGRATED_MASTER_DEGREE))
	    return DegreeType.BOLONHA_INTEGRATED_MASTER_DEGREE;
	if (isStudentOfDegreeType(DegreeType.BOLONHA_DEGREE))
	    return DegreeType.BOLONHA_DEGREE;
	return null;
    }

    private boolean isStudentOfDegreeType(DegreeType degreeType) {
	for (Registration registration : getRegistrationsByDegreeType(degreeType)) {
	    if (registration.isActive()) {
		StudentCurricularPlan scp = registration.getActiveStudentCurricularPlan();
		if (scp != null) {
		    return true;
		}
	    }
	}
	return false;
    }

    public boolean hasActiveRegistrationForOffice(Unit office) {
	Set<Registration> registrations = getRegistrationsSet();
	for (Registration registration : registrations) {
	    if (registration.isActiveForOffice(office)) {
		return true;
	    }
	}
	return false;
    }

    // Convenience method for invocation as bean.
    public boolean getHasActiveRegistrationForOffice() {
	Unit workingPlace = AccessControl.getPerson().getEmployee().getCurrentWorkingPlace();
	return hasActiveRegistrationForOffice(workingPlace);
    }

    public boolean hasRegistrationForOffice(final AdministrativeOffice administrativeOffice) {
	Set<Registration> registrations = getRegistrationsSet();
	for (Registration registration : registrations) {
	    if (registration.isForOffice(administrativeOffice)) {
		return true;
	    }
	}
	return false;
    }

    public boolean getHasRegistrationForOffice() {
	return hasRegistrationForOffice(AccessControl.getPerson().getEmployee()
		.getAdministrativeOffice());
    }

    public boolean attends(ExecutionCourse executionCourse) {
	for (final Registration registration : getRegistrationsSet()) {
	    if (registration.attends(executionCourse)) {
		return true;
	    }
	}
	return false;
    }

    public boolean hasAnyActiveRegistration() {
	for (final Registration registration : getRegistrationsSet()) {
	    if (registration.isActive()) {
		return true;
	    }
	}

	return false;
    }

    public void delete() {

	if (getRegistrationsCount() > 0) {
	    throw new DomainException("error.person.cannot.be.deleted");
	}

	if (getStudentDataByExecutionYearCount() > 0) {
	    throw new DomainException("error.person.cannot.be.deleted");
	}

	removePerson();
	removeRootDomainObject();
	deleteDomainObject();
    }

    public List<PaymentCode> getPaymentCodesBy(final PaymentCodeType paymentCodeType) {
	final List<PaymentCode> result = new ArrayList<PaymentCode>();
	for (final PaymentCode paymentCode : getPaymentCodesSet()) {
	    if (paymentCode.getType() == paymentCodeType) {
		result.add(paymentCode);
	    }
	}

	return result;
    }

    public PaymentCode getAvailablePaymentCodeBy(final PaymentCodeType paymentCodeType) {
	for (final PaymentCode paymentCode : getPaymentCodesSet()) {
	    if (paymentCode.isAvailableForReuse() && paymentCode.getType() == paymentCodeType) {
		return paymentCode;
	    }
	}

	return null;
    }

    public PaymentCode getPaymentCodeBy(final String code) {
	for (final PaymentCode paymentCode : getPaymentCodesSet()) {
	    if (paymentCode.getCode().equals(code)) {
		return paymentCode;
	    }
	}

	return null;
    }

    // TODO: This should be removed when master degree payments start using
    // Events and Posting Rules for payments
    public MasterDegreeInsurancePaymentCode calculateMasterDegreeInsurancePaymentCode(
	    final ExecutionYear executionYear) {
	if (!hasMasterDegreeInsurancePaymentCodeFor(executionYear)) {
	    return createMasterDegreeInsurancePaymentCode(executionYear);
	} else {
	    final MasterDegreeInsurancePaymentCode masterDegreeInsurancePaymentCode = getMasterDegreeInsurancePaymentCodeFor(executionYear);
	    final Money insuranceAmount = new Money(executionYear.getInsuranceValue()
		    .getAnnualValueBigDecimal());
	    masterDegreeInsurancePaymentCode.update(new YearMonthDay(),
		    calculateMasterDegreeInsurancePaymentCodeEndDate(executionYear), insuranceAmount,
		    insuranceAmount);

	    return masterDegreeInsurancePaymentCode;
	}
    }

    private MasterDegreeInsurancePaymentCode createMasterDegreeInsurancePaymentCode(
	    final ExecutionYear executionYear) {
	final Money insuranceAmount = new Money(executionYear.getInsuranceValue()
		.getAnnualValueBigDecimal());
	return MasterDegreeInsurancePaymentCode.create(new YearMonthDay(),
		calculateMasterDegreeInsurancePaymentCodeEndDate(executionYear), insuranceAmount,
		insuranceAmount, this, executionYear);
    }

    private YearMonthDay calculateMasterDegreeInsurancePaymentCodeEndDate(
	    final ExecutionYear executionYear) {
	final YearMonthDay insuranceEndDate = executionYear.getInsuranceValue().getEndDateYearMonthDay();
	final YearMonthDay now = new YearMonthDay();

	if (now.isAfter(insuranceEndDate)) {
	    final YearMonthDay nextMonth = now.plusMonths(1);
	    return new YearMonthDay(nextMonth.getYear(), nextMonth.getMonthOfYear(), 1).minusDays(1);
	} else {
	    return insuranceEndDate;
	}
    }

    private MasterDegreeInsurancePaymentCode getMasterDegreeInsurancePaymentCodeFor(
	    final ExecutionYear executionYear) {
	for (final PaymentCode paymentCode : getPaymentCodesSet()) {
	    if (paymentCode instanceof MasterDegreeInsurancePaymentCode) {
		final MasterDegreeInsurancePaymentCode masterDegreeInsurancePaymentCode = ((MasterDegreeInsurancePaymentCode) paymentCode);
		if (masterDegreeInsurancePaymentCode.getExecutionYear() == executionYear) {
		    return masterDegreeInsurancePaymentCode;
		}
	    }
	}

	return null;
    }

    private boolean hasMasterDegreeInsurancePaymentCodeFor(final ExecutionYear executionYear) {
	return getMasterDegreeInsurancePaymentCodeFor(executionYear) != null;
    }

    // TODO: this method should be refactored as soon as possible
    public boolean hasToPayMasterDegreeInsuranceFor(final ExecutionYear executionYear) {
	for (final Registration registration : getRegistrationsByDegreeType(DegreeType.MASTER_DEGREE)) {
	    if (!registration.isActive() || registration.getActiveStudentCurricularPlan() == null) {
		continue;
	    }

	    if (!registration.hasToPayMasterDegreeInsurance(executionYear)) {
		return false;
	    }

	}

	return true;
    }

    public Set<ExecutionPeriod> getEnroledExecutionPeriods() {
	Set<ExecutionPeriod> result = new TreeSet<ExecutionPeriod>(
		ExecutionPeriod.EXECUTION_PERIOD_COMPARATOR_BY_SEMESTER_AND_YEAR);
	for (Registration registration : getRegistrations()) {
	    result.addAll(registration.getEnrolmentsExecutionPeriods());
	}
	return result;
    }

    public Collection<StudentStatuteBean> getCurrentStatutes() {
	return getStatutes(ExecutionPeriod.readActualExecutionPeriod());
    }

    private Collection<StudentStatuteBean> getStatutes(ExecutionPeriod executionPeriod) {
	List<StudentStatuteBean> result = new ArrayList<StudentStatuteBean>();
	for (final StudentStatute statute : getStudentStatutesSet()) {
	    if (statute.isValidInExecutionPeriod(executionPeriod)) {
		result.add(new StudentStatuteBean(statute, executionPeriod));
	    }
	}

	if (isHandicapped()) {
	    result.add(new StudentStatuteBean(StudentStatuteType.HANDICAPPED, executionPeriod));
	}

	return result;
    }

    public Collection<StudentStatuteBean> getAllStatutes() {
	List<StudentStatuteBean> result = new ArrayList<StudentStatuteBean>();
	for (StudentStatute statute : getStudentStatutes()) {
	    result.add(new StudentStatuteBean(statute));
	}

	if (isHandicapped()) {
	    result.add(new StudentStatuteBean(StudentStatuteType.HANDICAPPED));
	}

	return result;
    }

    public Collection<StudentStatuteBean> getAllStatutesSplittedByExecutionPeriod() {
	List<StudentStatuteBean> result = new ArrayList<StudentStatuteBean>();
	for (ExecutionPeriod executionPeriod : getEnroledExecutionPeriods()) {
	    result.addAll(getStatutes(executionPeriod));
	}
	return result;
    }

    public void addApprovedEnrolments(final Collection<Enrolment> enrolments) {
	for (final Registration registration : getRegistrationsSet()) {
	    registration.addApprovedEnrolments(enrolments);
	}
    }

    public Set<Enrolment> getApprovedEnrolments() {
	Set<Enrolment> aprovedEnrolments = new HashSet<Enrolment>();
	for (Registration registration : getRegistrationsSet()) {
	    aprovedEnrolments.addAll(registration.getApprovedEnrolments());
	}
	return aprovedEnrolments;
    }

    public boolean isHandicapped() {
	for (Registration registration : getRegistrationsSet()) {
	    if (registration.getIngression() != null
		    && registration.getIngressionEnum().equals(Ingression.CNA07)) {
		return true;
	    }
	}
	return false;
    }

    public boolean getHasAnyBolonhaRegistration() {
	for (final Registration registration : getRegistrationsSet()) {
	    if (registration.getDegreeType().isBolonhaType()) {
		return true;
	    }
	}
	return false;
    }

    public Collection<StudentCurricularPlan> getAllStudentCurricularPlans() {
	Set<StudentCurricularPlan> result = new HashSet<StudentCurricularPlan>();
	for (Registration registration : getRegistrationsSet()) {
	    result.addAll(registration.getStudentCurricularPlansSet());
	}
	return result;
    }

    public Set<DistributedTest> getDistributedTestsByExecutionCourse(ExecutionCourse executionCourse) {
	Set<DistributedTest> result = new HashSet<DistributedTest>();
	for (final Registration registration : getRegistrationsSet()) {
	    for (StudentTestQuestion studentTestQuestion : registration.getStudentTestsQuestions()) {
		if (studentTestQuestion.getDistributedTest().getTestScope().getClassName().equals(
			ExecutionCourse.class.getName())
			&& studentTestQuestion.getDistributedTest().getTestScope().getKeyClass().equals(
				executionCourse.getIdInternal())) {
		    result.add(studentTestQuestion.getDistributedTest());
		}
	    }
	}
	return result;
    }

    public int countDistributedTestsByExecutionCourse(final ExecutionCourse executionCourse) {
	return getDistributedTestsByExecutionCourse(executionCourse).size();
    }

    public Attends readAttendByExecutionCourse(ExecutionCourse executionCourse) {
	for (final Registration registration : getRegistrationsSet()) {
	    Attends attends = registration.readRegistrationAttendByExecutionCourse(executionCourse);
	    if (attends != null) {
		return attends;
	    }
	}
	return null;
    }

    public List<Registration> getRegistrationsToEnrolByStudent() {
	final List<DegreeType> degreeTypesToEnrolByStudent = getDegreeTypesToEnrolByStudent();
	return getRegistrationsToEnrol(degreeTypesToEnrolByStudent);
    }

    public List<Registration> getRegistrationsToEnrolInShiftByStudent() {
	final List<DegreeType> degreeTypesToEnrolByStudent = getDegreeTypesToEnrolInShiftsByStudent();
	return getRegistrationsToEnrol(degreeTypesToEnrolByStudent);
    }

    private List<Registration> getRegistrationsToEnrol(List<DegreeType> degreeTypesToEnrolByStudent) {
	final List<Registration> result = new ArrayList<Registration>();
	for (final Registration registration : getRegistrations()) {
	    if (registration.isActive()
		    && degreeTypesToEnrolByStudent.contains(registration.getDegreeType())) {

		if (registration.hasStudentCandidacy()
			&& registration.getIngressionEnum() == Ingression.CIA2C) {
		    continue;
		}

		result.add(registration);
	    }
	}

	return result;
    }

    private List<DegreeType> getDegreeTypesToEnrolByStudent() {
	return Arrays.asList(new DegreeType[] { DegreeType.DEGREE, DegreeType.BOLONHA_DEGREE,
		DegreeType.BOLONHA_INTEGRATED_MASTER_DEGREE });
    }

    private List<DegreeType> getDegreeTypesToEnrolInShiftsByStudent() {
	return Arrays.asList(new DegreeType[] { DegreeType.DEGREE, DegreeType.BOLONHA_DEGREE,
		DegreeType.BOLONHA_INTEGRATED_MASTER_DEGREE, DegreeType.MASTER_DEGREE });
    }

    public boolean isCurrentlyEnroled(DegreeCurricularPlan degreeCurricularPlan) {
	for (Registration registration : getRegistrations()) {
	    if (!registration.isActive()) {
		continue;
	    }

	    StudentCurricularPlan lastStudentCurricularPlan = registration
		    .getLastStudentCurricularPlan();
	    if (lastStudentCurricularPlan == null) {
		continue;
	    }

	    if (lastStudentCurricularPlan.getDegreeCurricularPlan() != degreeCurricularPlan) {
		continue;
	    }

	    return true;
	}

	return false;
    }

    final public Enrolment getDissertationEnrolment() {
	return getDissertationEnrolment(null);
    }

    final public Enrolment getDissertationEnrolment(final DegreeCurricularPlan degreeCurricularPlan) {
	for (Registration registration : getRegistrations()) {
	    if (!registration.isActive()) {
		continue;
	    }

	    final Enrolment dissertationEnrolment = registration
		    .getDissertationEnrolment(degreeCurricularPlan);
	    if (dissertationEnrolment != null) {
		return dissertationEnrolment;
	    }
	}

	return null;
    }

    public boolean doesNotWantToRespondToInquiries() {
	for (final InquiriesStudentExecutionPeriod inquiriesStudentExecutionPeriod : getInquiriesStudentExecutionPeriodsSet()) {
	    if (inquiriesStudentExecutionPeriod.getExecutionPeriod().getState().equals(
		    PeriodState.CURRENT)
		    && inquiriesStudentExecutionPeriod.getDontWantToRespond().booleanValue()) {
		return true;
	    }
	}
	return false;
    }

    /**
     * -> Temporary overrides due migrations - Filter 'InTransition'
     * registrations -> Do not use this method to add new registrations directly
     * (use {@link addRegistrations} method)
     */
    @Override
    public List<Registration> getRegistrations() {
	final List<Registration> result = new ArrayList<Registration>();
	for (final Registration registration : super.getRegistrations()) {
	    if (!registration.isTransition()) {
		result.add(registration);
	    }
	}
	return Collections.unmodifiableList(result);
    }

    @Override
    public Set<Registration> getRegistrationsSet() {
	final Set<Registration> result = new HashSet<Registration>();
	for (final Registration registration : super.getRegistrationsSet()) {
	    if (!registration.isTransition()) {
		result.add(registration);
	    }
	}
	return Collections.unmodifiableSet(result);
    }

    @Override
    public Iterator<Registration> getRegistrationsIterator() {
	return getRegistrationsSet().iterator();
    }

    @Override
    public int getRegistrationsCount() {
	return getRegistrations().size();
    }

    public boolean hasTransitionRegistrations() {
	for (final Registration registration : super.getRegistrations()) {
	    if (registration.isTransition()) {
		return true;
	    }
	}

	return false;
    }

    @Checked("StudentPredicates.checkIfLoggedPersonIsStudentOwner")
    public List<Registration> getTransitionRegistrations() {
	final List<Registration> result = new ArrayList<Registration>();
	for (final Registration registration : super.getRegistrations()) {
	    if (registration.isTransition()) {
		result.add(registration);
	    }
	}
	return result;
    }

    @Checked("StudentPredicates.checkIfLoggedPersonIsCoordinator")
    public List<Registration> getTransitionRegistrationsForDegreeCurricularPlansManagedByCoordinator(
	    final Person coordinator) {
	final List<Registration> result = new ArrayList<Registration>();
	for (final Registration registration : super.getRegistrations()) {
	    if (registration.isTransition()
		    && coordinator.isCoordinatorFor(registration.getLastDegreeCurricularPlan(),
			    ExecutionYear.readCurrentExecutionYear())) {
		result.add(registration);
	    }
	}
	return result;
    }
}
