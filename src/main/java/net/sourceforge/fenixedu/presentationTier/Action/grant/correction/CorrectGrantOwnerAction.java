package net.sourceforge.fenixedu.presentationTier.Action.grant.correction;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.applicationTier.IUserView;
import net.sourceforge.fenixedu.applicationTier.Servico.commons.CloseLogin;
import net.sourceforge.fenixedu.applicationTier.Servico.grant.owner.EditGrantOwner;
import net.sourceforge.fenixedu.applicationTier.Servico.grant.owner.SearchGrantOwner;
import net.sourceforge.fenixedu.applicationTier.Servico.person.ChangePersonUsername;
import net.sourceforge.fenixedu.dataTransferObject.InfoPerson;
import net.sourceforge.fenixedu.dataTransferObject.grant.owner.InfoGrantOwner;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.person.IDDocumentType;
import net.sourceforge.fenixedu.domain.person.RoleType;
import net.sourceforge.fenixedu.presentationTier.Action.base.FenixDispatchAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

import pt.ist.fenixWebFramework.security.UserView;
import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixWebFramework.struts.annotations.Tile;

@Mapping(module = "facultyAdmOffice", path = "/correctGrantOwner", input = "/correctGrantOwner.do?page=0&method=prepareForm",
        attribute = "correctGrantOwner", formBean = "correctGrantOwner", scope = "request", parameter = "method")
@Forwards(value = { @Forward(name = "correct-grant-owner", path = "/facultyAdmOffice/grant/correction/grantOwnerCorrection.jsp",
        tileProperties = @Tile(title = "private.teachingstaffandresearcher.corrections.baggins")) })
public class CorrectGrantOwnerAction extends FenixDispatchAction {

    public ActionForward prepareForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        return mapping.findForward("correct-grant-owner");
    }

    public ActionForward changeAssociatedPerson(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Integer grantOwnerNumber = null;
        Integer documentIdNumber = null;
        IDDocumentType documentIdType = null;

        try {
            DynaValidatorForm correctGrantOwnerForm = (DynaValidatorForm) form;
            grantOwnerNumber = new Integer((String) correctGrantOwnerForm.get("grantOwnerNumber"));
            documentIdNumber = new Integer((String) correctGrantOwnerForm.get("documentIdNumber"));
            documentIdType = IDDocumentType.valueOf((String) correctGrantOwnerForm.get("documentIdType"));

        } catch (Exception e) {
            return setError(request, mapping, "errors.grant.correction.fillAllFields", null, null);
        }

        IUserView userView = UserView.getUser();

        // Read the grant owner

        List infoGrantOwnerList = SearchGrantOwner.run(null, null, null, grantOwnerNumber, new Boolean(true), null);

        if (infoGrantOwnerList.isEmpty() || infoGrantOwnerList.size() > 1) {
            return setError(request, mapping, "errors.grant.correction.unknownGrantOwner", null, null);
        }
        InfoGrantOwner infoGrantOwner = (InfoGrantOwner) infoGrantOwnerList.get(0);

        // Read the new person

        List infoPersonList =
                SearchGrantOwner.run(null, documentIdNumber.toString(), documentIdType, null, new Boolean(false), null);

        if (infoPersonList.isEmpty() || infoPersonList.size() > 1) {
            return setError(request, mapping, "errors.grant.correction.unknownPerson", null, null);
        }

        // Verify if the new person is already a grant owner
        InfoGrantOwner newInfoGrantOwner = (InfoGrantOwner) infoPersonList.get(0);
        InfoPerson oldInfoPerson = null;

        if (newInfoGrantOwner.getIdInternal() != null) {
            return setError(request, mapping, "errors.grant.correction.personAlreadyGrantOwner", null, null);
        }

        oldInfoPerson = infoGrantOwner.getPersonInfo();
        infoGrantOwner.setPersonInfo(newInfoGrantOwner.getPersonInfo());

        // Change username of the old person if is a "b***" to
        // INA(NumeroDocumentoIdentificacao)
        if (oldInfoPerson.getUsername().charAt(0) == 'B') {
            Person person = (Person) rootDomainObject.readPartyByOID(oldInfoPerson.getIdInternal());

            CloseLogin.run(person.getLoginIdentification());
        }

        // Change username of the new person if is a "INA***" to
        // B(GrantOwnerNumber)
        if (newInfoGrantOwner.getPersonInfo().getUsername().charAt(0) == 'I') {
            InfoPerson infoPerson = newInfoGrantOwner.getPersonInfo();
            String newUsernameNewPerson = "B";
            newUsernameNewPerson += infoGrantOwner.getGrantOwnerNumber().toString();

            ChangePersonUsername.run(newUsernameNewPerson, infoPerson.getIdInternal(), RoleType.GRANT_OWNER);
        }

        EditGrantOwner.run(infoGrantOwner);

        request.setAttribute("correctionNumber1", "yes");
        return mapping.findForward("correct-grant-owner");
    }
}