/*
 * Created on 5/Mai/2003
 *
 * 
 */
package DataBeans;


/**
 * @author Jo�o Mota
 *
 * 
 */
public class InfoSiteCurricularCourse implements ISiteComponent {

	private InfoCurriculum infoCurriculum;
	private InfoCurricularCourse infoCurricularCourse;
	

	/**
	 * @return
	 */
	public InfoCurricularCourse getInfoCurricularCourse() {
		return infoCurricularCourse;
	}

	/**
	 * @param infoCurricularCourse
	 */
	public void setInfoCurricularCourse(InfoCurricularCourse infoCurricularCourse) {
		this.infoCurricularCourse = infoCurricularCourse;
	}

	/**
	 * @return
	 */
	public InfoCurriculum getInfoCurriculum() {
		return infoCurriculum;
	}

	/**
	 * @param infoCurriculum
	 */
	public void setInfoCurriculum(InfoCurriculum infoCurriculum) {
		this.infoCurriculum = infoCurriculum;
	}

	

}
