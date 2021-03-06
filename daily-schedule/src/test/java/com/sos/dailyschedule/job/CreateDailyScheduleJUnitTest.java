package com.sos.dailyschedule.job;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.sos.JSHelper.Basics.JSToolBox;
import com.sos.JSHelper.Listener.JSListenerClass;
import com.sos.dailyschedule.db.DailyScheduleDBItem;
import com.sos.dailyschedule.db.DailyScheduleDBLayer;

public class CreateDailyScheduleJUnitTest extends JSToolBox {

    protected CreateDailyScheduleOptions objOptions = null;
    private static final Logger LOGGER = Logger.getLogger(CreateDailyScheduleJUnitTest.class);
    private CreateDailySchedule objE = null;

    public CreateDailyScheduleJUnitTest() {
        //
    }

    @Before
    public void setUp() throws Exception {
        objE = new CreateDailySchedule();
        objE.registerMessageListener(this);
        objOptions = objE.getOptions();
        JSListenerClass.bolLogDebugInformation = true;
        JSListenerClass.intMaxDebugLevel = 9;
    }

    @Test
    public void testExecute() throws Exception {
        try {
            HashMap pobjHM = new HashMap();
            pobjHM.put("CreateDailyScheduleOptionsSuperClass.scheduler_port", 4410);
            pobjHM.put("CreateDailyScheduleOptionsSuperClass.schedulerHostName", "galadriel.sos");
            pobjHM.put("CreateDailyScheduleOptionsSuperClass.dayOffset", 0);
            pobjHM.put("configurationFile", "R:/nobackup/junittests/hibernate/hibernate.cfg.xml");
            objE.getOptions().setAllOptions(pobjHM);
            assertEquals("", objOptions.scheduler_port.value(), 4410);
            objE.Execute();
            DailyScheduleDBLayer d = new DailyScheduleDBLayer("R:/nobackup/junittests/hibernate/hibernate.cfg.xml");
            d.getConnection().connect();
            d.getConnection().beginTransaction();
            Query query = d.getConnection().createQuery(" from DailyScheduleDBItem where job like :test");
            query.setParameter("test", "/sos/dailyschedule/CreateDaysSchedule");
            List calendarList = query.list();
            for (int i = 0; i < calendarList.size(); i++) {
                DailyScheduleDBItem calendarItem = (DailyScheduleDBItem) calendarList.get(i);
                if (i == 0) {
                    assertEquals("/sos/dailyschedule/CreateDaysSchedule", calendarItem.getJob());
                    break;
                }
            }
            d.getConnection().commit();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}