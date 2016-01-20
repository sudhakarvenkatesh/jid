package com.sos.scheduler.history.db;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class SchedulerOrderStepHistoryDBLayerTest {

    @SuppressWarnings("unused")
    private final String conClassName = "SchedulerOrderStepHistoryDBLayerTest";
    private SchedulerOrderStepHistoryDBLayer schedulerOrderStepHistoryDBLayer;
    private final String configurationFilename = "R:/nobackup/junittests/hibernate/hibernate_oracle.cfg.xml";
    private File configurationFile;

    public SchedulerOrderStepHistoryDBLayerTest() {
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        configurationFile = new File(configurationFilename);
        schedulerOrderStepHistoryDBLayer = new SchedulerOrderStepHistoryDBLayer(configurationFile);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSchedulerOrderStepHistoryDBLayer() {
        SchedulerOrderStepHistoryDBLayer d = new SchedulerOrderStepHistoryDBLayer(configurationFile);
    }

    @Test
    public void testDeleteString() throws ParseException {
        schedulerOrderStepHistoryDBLayer.filter.setExecutedFrom("2011-01-01 00:00:00");
        schedulerOrderStepHistoryDBLayer.filter.setExecutedTo("2011-10-01 00:00:00");
        // schedulerOrderStepHistoryDBLayer.filter.setSchedulerId("scheduler_4139");
        // schedulerOrderStepHistoryDBLayer.deleteFromTo();
        // int i = schedulerOrderStepHistoryDBLayer.deleteFromTo();
        // schedulerOrderStepHistoryDBLayer.commit();
        // assertEquals("testDeleteString fails...:",0,i);
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testGetSchedulerOrderStepHistoryList() throws ParseException {
        schedulerOrderStepHistoryDBLayer.filter.setExecutedFrom("2000-01-01 00:00:00");
        schedulerOrderStepHistoryDBLayer.filter.setExecutedTo("2020-01-01 00:00:00");
        List<SchedulerOrderStepHistoryDBItem> historyList = schedulerOrderStepHistoryDBLayer.getSchedulerOrderStepHistoryListFromTo(1);
        assertEquals("testGetSchedulerOrderStepHistoryList fails...:", 1, historyList.size());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testGetOrderStepHistoryItems() throws Exception {
        schedulerOrderStepHistoryDBLayer.filter.setExecutedFrom("2000-01-01 00:00:00");
        schedulerOrderStepHistoryDBLayer.filter.setExecutedTo(new Date());
        List<SchedulerOrderStepHistoryDBItem> historyList = schedulerOrderStepHistoryDBLayer.getSchedulerOrderStepHistoryListFromTo(1);
        assertEquals("testGetOrderStepHistoryList fails...:", 1, historyList.size());
    }

    /*
     * public void testSaveOrderStepHistory() throws Exception {
     * SchedulerOrderHistoryDBLayer schedulerOrderHistoryDBLayer = new
     * SchedulerOrderHistoryDBLayer(configurationFile);
     * List<SchedulerOrderHistoryDBItem> historyList =
     * schedulerOrderHistoryDBLayer.getOrderHistoryItems(1); Long order_key =
     * historyList.get(0).getHistoryId();
     * schedulerOrderHistoryDBLayer.beginTransaction();
     * SchedulerOrderHistoryDBItem
     * loadedhistory=schedulerOrderHistoryDBLayer.get(order_key);
     * assertNotNull(loadedhistory); schedulerOrderHistoryDBLayer.commit();
     * schedulerOrderStepHistoryDBLayer.beginTransaction();
     * schedulerOrderStepHistoryDBLayer.setWhereFrom(new Date());
     * schedulerOrderStepHistoryDBLayer.setWhereTo(new Date());
     * schedulerOrderStepHistoryDBLayer.deleteFromTo() ;
     * schedulerOrderStepHistoryDBLayer.commit();
     * schedulerOrderStepHistoryDBLayer.beginTransaction();
     * SchedulerOrderStepHistoryDBItem historyTable = new
     * SchedulerOrderStepHistoryDBItem(); Long myHistoryId = new Long(363475);
     * Long myStep = new Long(13); SchedulerOrderStepHistoryCompoundKey key =
     * new SchedulerOrderStepHistoryCompoundKey(myHistoryId,myStep);
     * historyTable.setId(key);
     * historyTable.setSchedulerOrderHistoryDBItem(loadedhistory);
     * historyTable.setTaskId(new Long(32));
     * historyTable.setErrorCode("Kabeljau"); historyTable.setState("state");
     * historyTable.setStartTime(new Date()); historyTable.setEndTime(new
     * Date()); schedulerOrderStepHistoryDBLayer.save(historyTable);
     * assertNotNull(historyTable.getId().getHistoryId());
     * schedulerOrderStepHistoryDBLayer.commit();
     * SchedulerOrderStepHistoryCompoundKey id=historyTable.getId();
     * schedulerOrderStepHistoryDBLayer.beginTransaction();
     * SchedulerOrderStepHistoryDBItem
     * history=schedulerOrderStepHistoryDBLayer.get(id); assertNotNull(history);
     * assertEquals("Kabeljau",history.getErrorCode());
     * schedulerOrderStepHistoryDBLayer.commit(); }
     */
}
