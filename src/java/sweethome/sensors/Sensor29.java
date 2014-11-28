package sweethome.sensors;

import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.container.OneWireContainer29;
import sweethome.HomeNet;
import sweethome.sensors.onewire.MemoryBank;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * <P> 1-Wire&#174 sensor for a Single Addressable Switch, DS2408.  This sensor
 * encapsulates the functionality of the 1-Wire family type <B>29</B> (hex)</P>
 *
 * <H3> Features </H3>
 * <UL>
 *   <LI> Eight channels of programmable I/O with open-drain outputs
 *   <LI> Logic level sensing of the PIO pin can be sensed
 *   <LI> Multiple DS2408's can be identified on a common 1-Wire bus and operated
 *        independently.
 *   <LI> Supports 1-Wire Conditional Search command with response controlled by
 *        programmable PIO conditions
 *   <LI> Supports Overdrive mode which boosts communication speed up to 142k bits
 *        per second.
 * </UL>
 *
 * <H3> Usage </H3>
 *
 *
 * @see com.dalsemi.onewire.container.OneWireSensor
 * @see com.dalsemi.onewire.container.SwitchContainer
 * @see com.dalsemi.onewire.container.OneWireContainer
 */
public class Sensor29 implements Sensor{
    private final OneWireContainer29 owd;

    private boolean stateRead    = false;
    private boolean registerRead = false;
    private byte[] state;
    private byte[] register;

    public Sensor29(OneWireContainer29 container){
        this.owd = container;
    }
    @Override
    public byte[] read() throws Exception {
        stateRead = false;
        return getState();
    }

    @Override
    public void close() {
        HomeNet.close((OneWireContainer) owd);
    }

    public Collection<MemoryBank> getMemoryBanks(){
        List<MemoryBank> banks = new ArrayList<>(3);

        // get a vector of the banks
        for (Enumeration<com.dalsemi.onewire.container.MemoryBank> bank_enum = owd.getMemoryBanks();
             bank_enum.hasMoreElements(); )
        {
            banks.add(new MemoryBank( bank_enum.nextElement()) );
        }
        return banks;
    }


    /**
     * Gets the number of channels supported by this switch.
     * Channel specific methods will use a channel number specified
     * by an integer from [0 to (<code>getNumberChannels()</code> - 1)].  Note that
     * all devices of the same family will not necessarily have the
     * same number of channels.
     *
     * @return the number of channels for this device
     *
     * @see com.dalsemi.onewire.container.OneWireSensor#readDevice()
     */
    public int getNumberChannels() throws OneWireException {
        byte[] state = getState();
        // check the 88h byte bits 6 and 7
        // 00 - 4 channels
        // 01 - 5 channels
        // 10 - 8 channels
        // 11 - 16 channels, which hasn't been implemented yet
        return owd.getNumberChannels(state);
    }

    /**
     * Checks if the channels of this switch support
     * level sensing.  If this method returns <code>true</code> then the
     * method <code>getLevel(int,byte[])</code> can be used.
     *
     * @return <code>true</code> if channels support level sensing
     *
     * @see #getLevel(int)
     */
    public boolean hasLevelSensing() {
        return owd.hasLevelSensing();
    }

    /**
     * Checks the sensed level on the indicated channel.
     * To avoid an exception, verify that this switch
     * has level sensing with the  <code>hasLevelSensing()</code>.
     * Level sensing means that the device can sense the logic
     * level on its PIO pin.
     *
     * @param channel channel to execute this operation, in the range [0 to (<code>getNumberChannels(byte[])</code> - 1)]
     * Usually this value is in range 0..7
     *
     * @return <code>true</code> if level sensed is 'high' and <code>false</code> if level sensed is 'low'
     *
     * @see com.dalsemi.onewire.container.OneWireSensor#readDevice()
     * @see #hasLevelSensing()
     */
    public boolean getLevel(int channel) throws OneWireException {
        byte[] state = getState();
        return owd.getLevel(channel,state);
    }

    /**
     * Checks if the channels of this switch are 'high side'
     * switches.  This indicates that when 'on' or <code>true</code>, the switch output is
     * connect to the 1-Wire data.  If this method returns  <code>false</code>
     * then when the switch is 'on' or <code>true</code>, the switch is connected
     * to ground.
     *
     * @return <code>true</code> if the switch is a 'high side' switch,
     *         <code>false</code> if the switch is a 'low side' switch
     *
     * @see #getLatchState(int)
     */
    public boolean isHighSideSwitch() {
        return owd.isHighSideSwitch();
    }

    /**
     * Checks the latch state of the indicated channel.
     *
     * @param channel channel to execute this operation, in the range [0 to (<code>getNumberChannels(byte[])</code> - 1)]
     *
     * @return <code>true</code> if channel latch is 'on'
     * or conducting and <code>false</code> if channel latch is 'off' and not
     * conducting.  Note that the actual output when the latch is 'on'
     * is returned from the <code>isHighSideSwitch()</code> method.
     *
     * @see com.dalsemi.onewire.container.OneWireSensor#readDevice()
     * @see #isHighSideSwitch()
     * @see #latchStateOn(int)
     * @see #latchStateOff(int)
     */
    public boolean getLatchState(int channel) throws OneWireException {
        byte[] state = getState();
        return owd.getLatchState(channel, state);
    }

    /**
     * Checks if the channels of this switch support
     * 'smart on'. Smart on is the ability to turn on a channel
     * such that only 1-Wire device on this channel are awake
     * and ready to do an operation.  This greatly reduces
     * the time to discover the device down a branch.
     * If this method returns <code>true</code> then the
     * method <code>setLatchState(int,boolean,boolean,byte[])</code>
     * can be used with the <code>doSmart</code> parameter <code>true</code>.
     *
     * @return <code>true</code> if channels support 'smart on'
     *
     * @see #latchStateOn(int)
     * @see #latchStateOff(int)
     */
    public boolean hasSmartOn() {
        return owd.hasSmartOn();
    }

    /**
     * Turn On the latch state of the indicated channel.
     *     latchState <code>true</code> to set the channel latch 'on'
     *     (conducting) and <code>false</code> to set the channel latch 'off' (not
     *     conducting).  Note that the actual output when the latch is 'on'
     *     is returned from the <code>isHighSideSwitch()</code> method.
     *
     * @param channel channel to execute this operation, in the range [0 to (<code>getNumberChannels(byte[])</code> - 1)]
     *
     * @see #hasSmartOn()
     * @see #getLatchState(int)
     * @see com.dalsemi.onewire.container.OneWireSensor#writeDevice(byte[])
     */
    public void latchStateOn(int channel) throws OneWireException {
        byte[] state = getState();
        owd.setLatchState(channel, true, true, state);
        owd.writeDevice(state);
    }

    /**
     * Turn Off the latch state of the indicated channel.
     *     latchState <code>true</code> to set the channel latch 'on'
     *     (conducting) and <code>false</code> to set the channel latch 'off' (not
     *     conducting).  Note that the actual output when the latch is 'on'
     *     is returned from the <code>isHighSideSwitch()</code> method.
     *
     * @param channel channel to execute this operation, in the range [0 to (<code>getNumberChannels(byte[])</code> - 1)]
     *
     * @see #hasSmartOn()
     * @see #getLatchState(int)
     * @see com.dalsemi.onewire.container.OneWireSensor#writeDevice(byte[])
     */
    public void latchStateOff(int channel) throws OneWireException {
        byte[] state = getState();
        owd.setLatchState(channel, false, true, state);
        owd.writeDevice(state);
    }

    /**
     * Write the latch state for all of the channels.
     *
     * @param set the state to set all of the channels, in the range [0 to (<code>getNumberChannels(byte[])</code> - 1)]
     *
     * @see #getLatchState(int)
     * @see com.dalsemi.onewire.container.OneWireSensor#writeDevice(byte[])
     */
    public void writeLatchState (int set) throws OneWireException {
        //byte[] bytes = ByteBuffer.allocate(4).putInt(set).array();
        writeLatchState((byte) set);
    }

    public void writeLatchState (byte set) throws OneWireException {
        byte[] state = getState();
        owd.setLatchState(set, state);
        owd.writeDevice(state);
    }

    /**
     * Clears the activity latches the next time possible.  For
     * example, on a DS2406/07, this happens the next time the
     * status is read with <code>readDevice()</code>.
     *
     * @throws OneWireException if this device does not support activity sensing
     *
     * @see com.dalsemi.onewire.container.OneWireSensor#readDevice()
     * @see #getSensedActivity(int)
     */
    public void clearActivity() throws OneWireException {
        owd.clearActivity();
    }

    /**
     * Checks if the channels of this switch support
     * activity sensing.  If this method returns <code>true</code> then the
     * method <code>getSensedActivity(int,byte[])</code> can be used.
     *
     * @return <code>true</code> if channels support activity sensing
     *
     * @see #getSensedActivity(int)
     * @see #clearActivity()
     */
    public boolean hasActivitySensing() {
        return owd.hasActivitySensing();
    }
    /**
     * Checks if the indicated channel has experienced activity.
     * This occurs when the level on the PIO pins changes.  To clear
     * the activity that is reported, call <code>clearActivity()</code>.
     * To avoid an exception, verify that this device supports activity
     * sensing by calling the method <code>hasActivitySensing()</code>.
     *
     * @param channel channel to execute this operation, in the range [0 to (<code>getNumberChannels(byte[])</code> - 1)]
     *
     * @return <code>true</code> if activity was detected and <code>false</code> if no activity was detected
     *
     * @throws OneWireException if this device does not have activity sensing
     *
     * @see #hasActivitySensing()
     * @see #clearActivity()
     */
    public boolean getSensedActivity(int channel) throws OneWireException {
        byte[] state = getState();
        return owd.getSensedActivity(channel, state);
    }

    /**
     * Turns the Reset mode on.
     *
     * @throws com.dalsemi.onewire.adapter.OneWireIOException on a 1-Wire communication error such as
     *         reading an incorrect CRC from a 1-Wire device.  This could be
     *         caused by a physical interruption in the 1-Wire Network due to
     *         shorts or a newly arriving 1-Wire device issuing a 'presence pulse'.
     * @throws OneWireException on a communication or setup error with the 1-Wire
     *         adapter
     */
    public void resetModeOn() throws OneWireException {
        byte[] register = getRegister();
        owd.setResetMode(register,true);
        owd.writeRegister(register);
    }

    /**
     * Turns the Reset mode off.
     *
     * @throws com.dalsemi.onewire.adapter.OneWireIOException on a 1-Wire communication error such as
     *         reading an incorrect CRC from a 1-Wire device.  This could be
     *         caused by a physical interruption in the 1-Wire Network due to
     *         shorts or a newly arriving 1-Wire device issuing a 'presence pulse'.
     * @throws OneWireException on a communication or setup error with the 1-Wire
     *         adapter
     */
    public void resetModeOff() throws OneWireException {
        byte[] register = getRegister();
        owd.setResetMode(register,false);
        owd.writeRegister(register);
    }

    /**
     * Retrieves the state of the VCC pin.  If the pin is powered 'TRUE' is
     * returned else 'FALSE' is returned if the pin is grounded.
     *
     * @return <code>true</code> if VCC is powered and <code>false</code> if it is
     *         grounded.
     *
     * @throws com.dalsemi.onewire.adapter.OneWireIOException on a 1-Wire communication error such as
     *         reading an incorrect CRC from a 1-Wire device.  This could be
     *         caused by a physical interruption in the 1-Wire Network due to
     *         shorts or a newly arriving 1-Wire device issuing a 'presence pulse'.
     * @throws OneWireException on a communication or setup error with the 1-Wire
     *         adapter
     */
    public boolean vcc() throws OneWireException {
        byte[] register = getRegister();
        return owd.getVCC(register);
    }

    /**
     * Checks if the Power On Reset if on and if so clears it.
     *
     */
    public void clearPowerOnReset() throws OneWireException {
        byte[] register = getRegister();
        owd.clearPowerOnReset(register);
        owd.writeRegister(register);
    }

    /**
     * Checks if the 'or' Condition Search is set and if not sets it.
     *
     */
    public void orConditionalSearch() throws OneWireException {
        byte[] register = getRegister();
        owd.orConditionalSearch(register);
        owd.writeRegister(register);
    }

    /**
     * Checks if the 'and' Conditional Search is set and if not sets it.
     *
     */
    public void andConditionalSearch() throws OneWireException {
        byte[] register = getRegister();
        owd.andConditionalSearch(register);
        owd.writeRegister(register);
    }

    /**
     * Checks if the 'PIO' Conditional Search is set for input and if not sets it.
     *
     */
    public void pioConditionalSearch() throws OneWireException {
        byte[] register = getRegister();
        owd.pioConditionalSearch(register);
        owd.writeRegister(register);
    }

    /**
     * Checks if the activity latches are set for Conditional Search and if not sets it.
     *
     */
    public void activityConditionalSearch() throws OneWireException {
        byte[] register = getRegister();
        owd.activityConditionalSearch(register);
        owd.writeRegister(register);
    }

    /**
     * Turn the channel passed to ON state depending on the set parameter for
     * responding to the Conditional Search.
     *
     * @param channel  current channel to set
     */
    public void setChannelMask(int channel) throws OneWireException {
        byte[] register = getRegister();
        owd.setChannelMask(channel, true, register);
        owd.writeRegister(register);
    }

    /**
     * Turn the channel passed to OFF state depending on the set parameter for
     * the correct polarity in the Conditional Search.
     *
     * @param channel  current channel to set
     */
    public void unsetChannelMask(int channel) throws OneWireException {
        byte[] register = getRegister();
        owd.setChannelMask(channel, false, register);
        owd.writeRegister(register);
    }

    /**
     * Turn the channel passed to ON state depending on the set parameter for
     * the correct polarity in the Conditional Search.
     *
     * @param channel  current channel to set
     */
    public void setChannelPolarity(int channel) throws OneWireException {
        byte[] register = getRegister();
        owd.setChannelPolarity(channel, true, register);
        owd.writeRegister(register);
    }

    /**
     * Turn the channel passed to OFF state depending on the set parameter for
     * responding to the Conditional Search.
     *
     * @param channel  current channel to set
     */
    public void unsetChannelPolarity(int channel) throws OneWireException {
        byte[] register = getRegister();
        owd.setChannelPolarity(channel, false, register);
        owd.writeRegister(register);
    }

    /**
     * Retrieves the information if the channel is masked for the Conditional Search.
     *
     * @param channel  current channel to set
     *
     * @return <code>true</code> if the channel is masked and <code>false</code> other wise.
     */
    public boolean getChannelMask(int channel) throws OneWireException {
        byte[] register = getRegister();
        return owd.getChannelMask(channel,register);
    }

    /**
     * Retrieves the polarity of the channel for the Conditional Search.
     *
     * @param channel  current channel to set
     *
     * @return <code>true</code> if the channel is masked and <code>false</code> other wise.
     */
    public boolean getChannelPolarity(int channel) throws OneWireException {
        byte[] register = getRegister();
        return owd.getChannelPolarity(channel, register);
    }

    public byte[] getState() throws OneWireException {
        if(!stateRead)
        {
            state = owd.readDevice();
            stateRead = true;
        }
        return state;
    }

    private byte[] getRegister() throws OneWireException {
        if(!registerRead)
        {
            register = owd.readRegister();
            registerRead = true;
        }
        return register;
    }
}
