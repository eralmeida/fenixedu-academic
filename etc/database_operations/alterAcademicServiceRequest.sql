alter table `ACADEMIC_SERVICE_REQUEST` add `DEFERRED` tinyint(1);
update ACADEMIC_SERVICE_REQUEST set DEFERRED = 0 where ID_INTERNAL=730632 and REQUEST_DATE='2010-06-16 10:38:00';
