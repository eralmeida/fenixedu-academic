package ServidorPersistente;

import java.util.ArrayList;

import Dominio.IBranch;
import Dominio.ICurricularCourse;
import Dominio.ICurricularCourseScope;
import Dominio.ICurricularSemester;
import ServidorPersistente.exceptions.ExistingPersistentException;

/**
 * @author dcs-rjao
 *
 * 24/Mar/2003
 */

public interface IPersistentCurricularCourseScope extends IPersistentObject {

		public void deleteAll() throws ExcepcaoPersistencia;
		public void lockWrite(ICurricularCourseScope curricularCourseScopeToWrite) throws ExcepcaoPersistencia, ExistingPersistentException;
		public void delete(ICurricularCourseScope curricularCourseScope) throws ExcepcaoPersistencia;
		public ICurricularCourseScope readCurricularCourseScopeByCurricularCourseAndCurricularSemesterAndBranch(ICurricularCourse curricularCourse, ICurricularSemester curricularSemester, IBranch branch) throws ExcepcaoPersistencia;
		public ArrayList readAll() throws ExcepcaoPersistencia;
}