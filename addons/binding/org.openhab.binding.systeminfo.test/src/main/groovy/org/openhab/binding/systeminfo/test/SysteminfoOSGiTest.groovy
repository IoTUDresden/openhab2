/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.systeminfo.test

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*

import org.eclipse.smarthome.config.core.Configuration
import org.eclipse.smarthome.config.discovery.DiscoveryResult
import org.eclipse.smarthome.config.discovery.DiscoveryService
import org.eclipse.smarthome.config.discovery.inbox.Inbox
import org.eclipse.smarthome.config.discovery.inbox.InboxFilterCriteria
import org.eclipse.smarthome.core.items.GenericItem
import org.eclipse.smarthome.core.items.ItemRegistry
import org.eclipse.smarthome.core.library.items.NumberItem
import org.eclipse.smarthome.core.library.items.StringItem
import org.eclipse.smarthome.core.library.types.DecimalType
import org.eclipse.smarthome.core.library.types.OnOffType
import org.eclipse.smarthome.core.library.types.StringType
import org.eclipse.smarthome.core.thing.Channel
import org.eclipse.smarthome.core.thing.ChannelUID
import org.eclipse.smarthome.core.thing.ManagedThingProvider
import org.eclipse.smarthome.core.thing.Thing
import org.eclipse.smarthome.core.thing.ThingProvider
import org.eclipse.smarthome.core.thing.ThingRegistry
import org.eclipse.smarthome.core.thing.ThingStatus
import org.eclipse.smarthome.core.thing.ThingStatusDetail
import org.eclipse.smarthome.core.thing.ThingTypeUID
import org.eclipse.smarthome.core.thing.ThingUID
import org.eclipse.smarthome.core.thing.binding.ThingHandler
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder
import org.eclipse.smarthome.core.thing.link.ItemChannelLink
import org.eclipse.smarthome.core.thing.link.ManagedItemChannelLinkProvider
import org.eclipse.smarthome.core.types.Command
import org.eclipse.smarthome.core.types.RefreshType
import org.eclipse.smarthome.core.types.State
import org.eclipse.smarthome.core.types.UnDefType
import org.eclipse.smarthome.test.OSGiTest
import org.eclipse.smarthome.test.storage.VolatileStorageService
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.experimental.categories.Category
import org.openhab.binding.systeminfo.SysteminfoBindingConstants
import org.openhab.binding.systeminfo.discovery.SysteminfoDiscoveryService
import org.openhab.binding.systeminfo.handler.SysteminfoHandler
/**
 * OSGi tests for the {@link SysteminfoHandler}
 *
 * @author Svilen Valkanov
 *
 */

class SysteminfoOSGiTest extends OSGiTest{
    def  DEFAULT_TEST_THING_NAME = "work";
    def  DEFAULT_TEST_ITEM_NAME = "test"
    def DEFAULT_CHANNEL_TEST_PRIORITY = "High"
    def DEFAULT_TEST_CHANNEL_ID = SysteminfoBindingConstants.CHANNEL_CPU_LOAD
    def DEFAULT_THING_INITIALIZE_MAX_TIME = 3000

    Thing systemInfoThing

    ManagedThingProvider managedThingProvider;
    ThingRegistry thingRegistry;
    ItemRegistry itemRegistry;

    /**
     * Refresh time in seconds for tasks with priority High.
     * Default value for the parameter interval_high in the thing configuration
     */
    int DEFAULT_TEST_INTERVAL_HIGH = 1
    /**
     * Refresh time in seconds for tasks with priority Medium.
     */
    int DEFAULT_TEST_INTERVAL_MEDIUM = 3

    @Before
    public void setUp ()  {
        VolatileStorageService volatileStorageService = new VolatileStorageService()
        registerService(volatileStorageService)
        managedThingProvider = getService(ThingProvider, ManagedThingProvider)
        assertThat managedThingProvider, is(notNullValue())

        thingRegistry = getService(ThingRegistry)
        assertThat thingRegistry, is(notNullValue())

        itemRegistry = getService(ItemRegistry)
        assertThat itemRegistry, is(notNullValue())
    }

    private void initializeThingWithChannelAndPriority(String channelID,String acceptedItemType,String priority) {
        Configuration thingConfig = new Configuration()
        thingConfig.put(SysteminfoBindingConstants.HIGH_PRIORITY_REFRESH_TIME, new BigDecimal(DEFAULT_TEST_INTERVAL_HIGH))
        thingConfig.put(SysteminfoBindingConstants.MEDIUM_PRIORITY_REFRESH_TIME, new BigDecimal(DEFAULT_TEST_INTERVAL_MEDIUM))

        initializeThing(thingConfig,channelID,acceptedItemType,priority)
    }

    private void initializeThingWithConfiguration(Configuration config) {
        String priority = DEFAULT_CHANNEL_TEST_PRIORITY
        String channelID = DEFAULT_TEST_CHANNEL_ID
        String acceptedItemType = "String";

        initializeThing(config,channelID,acceptedItemType,priority)
    }

    private void initializeThingWithChannel(String channelID,String acceptedItemType) {
        Configuration thingConfig = new Configuration()
        thingConfig.put(SysteminfoBindingConstants.HIGH_PRIORITY_REFRESH_TIME, new BigDecimal(DEFAULT_TEST_INTERVAL_HIGH))
        thingConfig.put(SysteminfoBindingConstants.MEDIUM_PRIORITY_REFRESH_TIME, new BigDecimal(DEFAULT_TEST_INTERVAL_MEDIUM))

        String priority = DEFAULT_CHANNEL_TEST_PRIORITY;
        initializeThing(thingConfig,channelID,acceptedItemType,priority)
    }

    private void initializeThing(Configuration thingConfiguration,String channelID,String acceptedItemType,String priority) {
        ThingTypeUID thingTypeUID = SysteminfoBindingConstants.THING_TYPE_COMPUTER;
        ThingUID thingUID = new ThingUID(thingTypeUID,DEFAULT_TEST_THING_NAME);

        ChannelUID channelUID = new ChannelUID(thingUID,channelID)
        Configuration channelConfig  = new Configuration()
        channelConfig.put("priority", priority)
        Channel channel = new Channel(channelUID,acceptedItemType,channelConfig)

        systemInfoThing = ThingBuilder.create(thingTypeUID,thingUID).withConfiguration(thingConfiguration).withChannel(channel).build();

        managedThingProvider.add(systemInfoThing)

        waitForAssert({
            SysteminfoHandler thingHandler = getService(ThingHandler,SysteminfoHandler)
            assertThat thingHandler, is(notNullValue())
        },2000)

        waitForAssert({
            assertThat "Thing is not initilized, before an Item is created", systemInfoThing.getStatus(),
                    anyOf(equalTo(ThingStatus.OFFLINE), equalTo(ThingStatus.ONLINE))
        },DEFAULT_THING_INITIALIZE_MAX_TIME)

        intializeItem(channelUID,DEFAULT_TEST_ITEM_NAME,acceptedItemType)
    }


    private void testItemStateIsUpdated(String acceptedItemType,String itemName,String priority) {
        testItemState(acceptedItemType,itemName,false,priority)
    }

    private void testItemStateIsNull (String acceptedItemType,String itemName,String priority) {
        testItemState(acceptedItemType,itemName,true,priority)
    }

    private void testItemState(String acceptedItemType,String itemName, boolean isNullExpected,String priority) {
        waitForAssert({
            def thingStatusDetail = systemInfoThing.getStatusInfo().getStatusDetail()
            def description = systemInfoThing.getStatusInfo().getDescription();
            assertThat  "Thing status detail is {$thingStatusDetail} with description {$description}",systemInfoThing.getStatus(), is(equalTo(ThingStatus.ONLINE))
        }, DEFAULT_THING_INITIALIZE_MAX_TIME)
        //The binding starts all refresh tasks in SysteminfoHandler.scheduleUpdates() after this delay !
        sleep(SysteminfoHandler.WAIT_TIME_CHANNEL_ITEM_LINK_INIT  * 1000)

        def Items = itemRegistry.getItems();
        def GenericItem item = itemRegistry.getItem(itemName) as GenericItem

        int waitTime;
        if(priority.equals("High")) {
            waitTime= DEFAULT_TEST_INTERVAL_HIGH * 1000;
        } else if(priority.equals("Medium")) {
            waitTime = DEFAULT_TEST_INTERVAL_MEDIUM * 1000;
        } else {
            waitTime = 100;
        }

        if(isNullExpected) {
            sleep(waitTime)
            assertThat item.getState(), is(equalTo(UnDefType.NULL))
        } else {
            waitForAssert({
                State itemState = item.getState()
                assertThat itemState, not (equalTo(UnDefType.NULL))
                if(acceptedItemType.equals("Number")) {
                    assertThat itemState, isA (DecimalType)
                } else if(acceptedItemType.equals("String")){
                    assertThat itemState, isA (StringType)
                } else {
                    fail "Test might not be set up correctly! Check if 'acceptedItemType' in the test case is set correctly !"
                }
            },waitTime)
        }
    }

    private void intializeItem (ChannelUID channelUID,String itemName, String acceptedItemType) {
        GenericItem itemType;
        if(acceptedItemType.equals("Number")) {
            itemType = new NumberItem(itemName)
        } else if(acceptedItemType.equals("String")){
            itemType = new StringItem(itemName)
        }
        itemRegistry.add(itemType)

        def ManagedItemChannelLinkProvider itemChannelLinkProvider = getService(ManagedItemChannelLinkProvider)
        assertThat itemChannelLinkProvider, is(notNullValue())

        ThingUID thingUID = systemInfoThing.getUID()
        itemChannelLinkProvider.add(new ItemChannelLink(itemName, channelUID))
    }

    @Test
    public void 'assert invalid thing configuration values are handled'() {
        Configuration configuration = new Configuration()

        //invalid value - must be positive
        int refreshIntervalHigh = -5;
        configuration.put(SysteminfoBindingConstants.HIGH_PRIORITY_REFRESH_TIME, new BigDecimal(refreshIntervalHigh))

        int refreshIntervalMedium = 3
        configuration.put(SysteminfoBindingConstants.MEDIUM_PRIORITY_REFRESH_TIME, new BigDecimal(refreshIntervalMedium))
        initializeThingWithConfiguration(configuration)

        testInvalidConfiguration()
    }

    private void testInvalidConfiguration() {
        waitForAssert({
            assertThat  "Invalid configuratuin is used !", systemInfoThing.getStatus(), is(equalTo(ThingStatus.OFFLINE))
            assertThat systemInfoThing.getStatusInfo().getStatusDetail() , is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR))
            assertThat systemInfoThing.getStatusInfo().getDescription(), is(equalTo("Thing can not be initialized! Configuration is invalid !"))
        }, DEFAULT_THING_INITIALIZE_MAX_TIME)
    }

    @Test
    public void 'assert low priority channel is updated on REFESH' () {
        testItemStateOnCommand(RefreshType.REFRESH,true)
    }

    @Test
    public void 'assert low priority channel is not updated on not supported command' () {
        testItemStateOnCommand(OnOffType.ON,false)
    }

    @Test
    public void 'assert medium priority channel is updated' () {
        String channnelID = DEFAULT_TEST_CHANNEL_ID;
        String acceptedItemType = "Number";
        String priority = "Medium"

        initializeThingWithChannelAndPriority(channnelID, acceptedItemType,priority)
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,priority);
    }

    private void testItemStateOnCommand(Command command,boolean mustItemStateChange) {
        String channnelID = DEFAULT_TEST_CHANNEL_ID
        String acceptedItemType = "Number"

        //Priority is set to low, so after initialize () channel state will be updated only on REFRESH
        String priority = "Low"
        initializeThingWithChannelAndPriority(channnelID, acceptedItemType,priority)

        //new item is created
        Channel channel = systemInfoThing.getChannel(channnelID);
        ChannelUID channelUID = channel.getUID()
        String newItemName = "New_item";

        //New item is initialized after the first refresh task has finished
        sleep(SysteminfoHandler.WAIT_TIME_CHANNEL_ITEM_LINK_INIT * 1000 + DEFAULT_THING_INITIALIZE_MAX_TIME)

        intializeItem(channelUID,newItemName,acceptedItemType)

        //New item state is null
        testItemStateIsNull(acceptedItemType,newItemName,priority)

        //post command
        systemInfoThing.handler.handleCommand(channelUID,command)

        if(mustItemStateChange) {
            testItemStateIsUpdated(acceptedItemType,newItemName,priority)
        } else {
            testItemStateIsNull(acceptedItemType,newItemName,priority)
        }
    }

    @Test
    public void 'assert state of not existing device is not updated' () {
        int deviceIndex = 520;
        String channnelID = "network$deviceIndex#mac"
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsNull(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }
    @Category(PlatformDependentTestsInterface.class)
    @Test
    public void 'assert state of second device is updated' () {
        //This test assumes that at least 2 network interfaces are present on the test platform
        int deviceIndex = 1;
        String channnelID = "network$deviceIndex#mac"
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel cpu#load is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_CPU_LOAD
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel cpu#load1 is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_CPU_LOAD_1
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel cpu#load5 is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_CPU_LOAD_5
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel cpu#load15 is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_CPU_LOAD_15;
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel cpu#threads is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_CPU_THREADS;
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel cpu#uptime is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_CPU_UPTIME;
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel cpu#description is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_CPU_DESCRIPTION;
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel cpu#name is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_CPU_NAME;
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel memory#available is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_MEMORY_AVAILABLE
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel memory#used is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_MEMORY_USED
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel memory#total is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_MEMORY_TOTAL
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel memory#availablePercent is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_MEMORY_AVAILABLE_PERCENT
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel swap#available is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_SWAP_AVAILABLE
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel swap#used is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_SWAP_USED
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel swap#total is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_SWAP_TOTAL
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel swap#availablePercent is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_SWAP_AVAILABLE_PERCENT
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel storage#name is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_STORAGE_NAME
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel storage#type is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_STORAGE_TYPE
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel storage#description is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_STORAGE_DESCRIPTION
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel storage#available is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_STORAGE_AVAILABLE
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel storage#used is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_STORAGE_USED
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel storage#total is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_STORAGE_TOTAL
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel storage#availablePercent is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_STORAGE_AVAILABLE_PERCENT
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel drive#name is updated' () {
        String channelID = SysteminfoBindingConstants.CHANNEL_DRIVE_NAME;
        String acceptedItemType = "String";

        initializeThingWithChannel(channelID,acceptedItemType)
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel drive#model is updated' () {
        String channelID = SysteminfoBindingConstants.CHANNEL_DRIVE_MODEL;
        String acceptedItemType = "String";

        initializeThingWithChannel(channelID,acceptedItemType)
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel drive#serial is updated' () {
        String channelID = SysteminfoBindingConstants.CHANNEL_DRIVE_SERIAL;
        String acceptedItemType = "String";

        initializeThingWithChannel(channelID,acceptedItemType)
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel sensors#cpuTemp is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_SENSORS_CPU_TEMPERATURE
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Test
    public void 'assert channel sensors#cpuVoltage is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_SENOSRS_CPU_VOLTAGE
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    //Jenskins's machine has no CPU Fan
    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel sensors#fanSpeed is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_SENSORS_FAN_SPEED
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    //Jenskins's machine has no battery
    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel battery#name is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_BATTERY_NAME
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    //Jenskins's machine has no battery
    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel battery#remainingCapacity is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_BATTERY_REMAINING_CAPACITY
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    //Jenskins's machine has no battery
    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel battery#remainingTime is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_BATTERY_REMAINING_TIME
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    //Jenskins's machine has no display
    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel display#information is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_DISPLAY_INFORMATION
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Ignore
    @Test
    public void 'assert channel network#ip is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_NETWORK_IP
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'asssert channel network#mac is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_NETWORK_MAC
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'asssert channel network#dataSent is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_NETWORK_DATA_SENT
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'asssert channel network#dataReceived is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_NETWORK_DATA_RECEIVED
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'asssert channel network#packagesSent is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_NETWORK_PACKAGES_SENT
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'asssert channel network#packagesReceived is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_NETWORK_PACKAGES_RECEIVED
        String acceptedItemType = "Number";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel network#networkName is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_NETWORK_NAME
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    @Category(org.openhab.binding.systeminfo.test.PlatformDependentTestsInterface.class)
    @Test
    public void 'assert channel network#networkDisplayName is updated' () {
        String channnelID = SysteminfoBindingConstants.CHANNEL_NETWORK_ADAPTER_NAME
        String acceptedItemType = "String";

        initializeThingWithChannel(channnelID,acceptedItemType);
        testItemStateIsUpdated(acceptedItemType,DEFAULT_TEST_ITEM_NAME,DEFAULT_CHANNEL_TEST_PRIORITY);
    }

    class SysteminfoDiscoveryServiceMock extends SysteminfoDiscoveryService {
        String hostname;
        SysteminfoDiscoveryServiceMock(String hostname) {
            super()
            this.hostname = hostname;
        }

        @Override
        protected String getHostName() throws UnknownHostException{
            if(hostname.equals("unresolved")) {
                throw new UnknownHostException()
            }
            return hostname;
        }
    }

    @Test
    public void 'test discovery with invalid hostname' () {
        String hostname = "Hilo.fritz.box"
        String expectedHostname = "Hilo_fritz_box"

        testDiscoveryService(expectedHostname, hostname)
    }

    @Test
    public void 'test discovery with valid hostname' () {
        String hostname = "MyComputer"
        String expectedHostname = "MyComputer"

        testDiscoveryService(expectedHostname,hostname)
    }

    @Test
    public void 'test discovery with unresolved hostname' () {
        String hostname = "unresolved"
        String expectedHostname = SysteminfoDiscoveryService.DEFAULT_THING_ID

        testDiscoveryService(expectedHostname,hostname)
    }

    @Test
    public void 'test discovery with empty hostname string' () {
        String hostname = ""
        String expectedHostname = SysteminfoDiscoveryService.DEFAULT_THING_ID

        testDiscoveryService(expectedHostname,hostname)
    }


    private void testDiscoveryService(String expectedHostname, String hostname) {
        SysteminfoDiscoveryService discoveryService = getService(DiscoveryService,SysteminfoDiscoveryService)
        waitForAssert {
            assertThat discoveryService, is(notNullValue())
        }
        SysteminfoDiscoveryService discoveryServiceMock = new SysteminfoDiscoveryServiceMock(hostname)
        unregisterService(discoveryService)
        registerService(discoveryServiceMock, DiscoveryService.class.getName(), new Hashtable())

        ThingTypeUID computerType = SysteminfoBindingConstants.THING_TYPE_COMPUTER;
        ThingUID computerUID = new ThingUID(computerType, expectedHostname);

        discoveryServiceMock.startScan();

        Inbox inbox = getService(Inbox)
        assertThat inbox, is(notNullValue())

        waitForAssert {
            List<DiscoveryResult> results = inbox.get(new InboxFilterCriteria(computerUID, null))
            assertFalse "No Thing with UID " + computerUID.getAsString() + " in inbox", results.isEmpty()
        }

        inbox.approve(computerUID, SysteminfoDiscoveryService.DEFAULT_THING_LABEL)

        waitForAssert {
            systemInfoThing = thingRegistry.get(computerUID)
            assertThat systemInfoThing, is (notNullValue())
        }
    }

    @After
    public void tearDown () {

        if(systemInfoThing != null){
            // Remove the systeminfo thing. The handler will be also disposed automatically
            Thing removedThing = thingRegistry.forceRemove(systemInfoThing.getUID())
            assertThat("The systeminfo thing cannot be deleted",removedThing,is(notNullValue()))
        }
        waitForAssert({
            assertThat getService(ThingHandler, SysteminfoHandler), is(nullValue())
        }, 1000)

    }
}
