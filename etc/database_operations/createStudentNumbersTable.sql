
SET AUTOCOMMIT = 0;
START TRANSACTION;

alter table `STUDENT` add column `OID_STUDENT_NUMBER` bigint(20), add index (OID_STUDENT_NUMBER);

create table `STUDENT_NUMBER` (
  `ID_INTERNAL` int(11) NOT NULL auto_increment,
  `OID` bigint(20) DEFAULT NULL,
  `OID_ROOT_DOMAIN_OBJECT` bigint(20) DEFAULT NULL,
  `OID_STUDENT` bigint(20) NOT NULL,
  `NUMBER` int(11) NOT NULL,
  primary key (ID_INTERNAL),
  index (OID),
  index (OID_ROOT_DOMAIN_OBJECT),
  index (OID_STUDENT)
) type=InnoDB, character set latin1 ;


SELECT @max_value:=null; SELECT @max_value:=MAX(FF$DOMAIN_CLASS_INFO.DOMAIN_CLASS_ID) + 1 FROM FF$DOMAIN_CLASS_INFO;
INSERT IGNORE INTO FF$DOMAIN_CLASS_INFO VALUES ("net.sourceforge.fenixedu.domain.student.StudentNumber", @max_value);

INSERT INTO STUDENT_NUMBER (OID_STUDENT, NUMBER) SELECT S.OID, S.NUMBER FROM STUDENT S WHERE S.NUMBER IS NOT NULL AND S.NUMBER != 0;

SELECT @xpto:=null;SELECT @xpto:=FF$DOMAIN_CLASS_INFO.DOMAIN_CLASS_ID from FF$DOMAIN_CLASS_INFO where FF$DOMAIN_CLASS_INFO.DOMAIN_CLASS_NAME = "net.sourceforge.fenixedu.domain.student.StudentNumber";
UPDATE STUDENT_NUMBER SET OID = (@xpto << 32) + STUDENT_NUMBER.ID_INTERNAL;

UPDATE STUDENT S, STUDENT_NUMBER SN SET S.OID_STUDENT_NUMBER = SN.OID, SN.OID_ROOT_DOMAIN_OBJECT = S.OID_ROOT_DOMAIN_OBJECT WHERE S.OID = SN.OID_STUDENT;

ALTER TABLE STUDENT_NUMBER CHANGE COLUMN OID OID bigint(20) NOT NULL, CHANGE COLUMN OID_ROOT_DOMAIN_OBJECT OID_ROOT_DOMAIN_OBJECT bigint(20) NOT NULL;

COMMIT;
