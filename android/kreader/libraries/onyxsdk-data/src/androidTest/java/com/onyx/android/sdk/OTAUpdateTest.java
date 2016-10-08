package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.v1.OnyxOTAService;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/23.
 */
public class OTAUpdateTest extends ApplicationTestCase<Application> {

    private static OnyxOTAService service;

    public OTAUpdateTest() {
        super(Application.class);
    }

    private final OnyxOTAService getService() {
        if (service == null) {
            CloudManager cloudManager = new CloudManager();
            service = ServiceFactory.getOTAService(cloudManager.getCloudConf().getApiBase());
        }
        return service;
    }

    public void testGetReleaseFirmware() throws Exception {
        Firmware firmware = Firmware.currentFirmware();
        Response<Firmware> response = getService().ReleaseFirmwareUpdate(JSON.toJSONString(firmware)).execute();
        assertNotNull(response);
        if (response.isSuccessful()) {
            assertNotNull(response.body());
            Firmware testFirmware = response.body();
            assertEquals(firmware.fwType, testFirmware.fwType);
            assertEquals(firmware.buildType, testFirmware.buildType);
        }
    }

    public void testGetTestFirmware() throws Exception {
        Firmware firmware = Firmware.currentFirmware();
        firmware.updateTestingBuildParameters();
        Response<Firmware> response = getService().testFirmwareUpdate(JSON.toJSONString(firmware)).execute();
        assertNotNull(response);
        if (response.isSuccessful()) {
            assertNotNull(response.body());
            Firmware testFirmware = response.body();
            assertEquals(firmware.fwType, testFirmware.fwType);
            assertEquals(firmware.buildType, testFirmware.buildType);
        }
    }

    public void testApplicationUpdate() throws Exception {
        ApplicationUpdate update = new ApplicationUpdate();
        update.packageName = "com.onyx";

        //test one
        Response<ApplicationUpdate> response = getService().getUpdateAppInfo(JSON.toJSONString(update)).execute();
        assertNotNull(response);
        assertNotNull(response.body());
        ApplicationUpdate responseUpdater = response.body();
        assertEquals(responseUpdater.packageName, update.packageName);
        assertNotNull(responseUpdater.versionName);
        assertTrue(responseUpdater.downloadUrlList.length > 0);
        assertTrue(responseUpdater.changeLogs.keySet().size() > 0);
        assertTrue(responseUpdater.changeLogs.values().size() > 0);

        //test batch query
        Response<List<ApplicationUpdate>> responseList = getService().getUpdateAppInfoList(null).execute();
        assertNotNull(responseList);
        assertNotNull(responseList.body());
        List<ApplicationUpdate> updaterList = responseList.body();
        assertTrue(updaterList.size() >= 1);
        for (ApplicationUpdate applicationUpdate : updaterList) {
            assertTrue(applicationUpdate.packageName.contains("onyx"));
        }
    }
}
