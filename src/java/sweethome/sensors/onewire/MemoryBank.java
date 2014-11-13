package sweethome.sensors.onewire;

import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.OneWireIOException;
import com.dalsemi.onewire.container.PagedMemoryBank;
import sweethome.sensors.Utils;

public class MemoryBank implements com.dalsemi.onewire.container.MemoryBank {
    private final com.dalsemi.onewire.container.MemoryBank bank;

    public MemoryBank(com.dalsemi.onewire.container.MemoryBank bank) {
        this.bank = bank;
    }

    @Override
    public String getBankDescription() {
        return bank.getBankDescription();
    }

    @Override
    public boolean isGeneralPurposeMemory() {
        return bank.isGeneralPurposeMemory();
    }

    @Override
    public int getSize() {
        return bank.getSize();
    }

    @Override
    public boolean isReadWrite() {
        return bank.isReadWrite();
    }

    @Override
    public boolean isWriteOnce() {
        return bank.isWriteOnce();
    }

    @Override
    public boolean isReadOnly() {
        return bank.isReadOnly();
    }

    @Override
    public boolean isNonVolatile() {
        return bank.isNonVolatile();
    }

    @Override
    public boolean needsProgramPulse() {
        return bank.needsProgramPulse();
    }

    @Override
    public boolean needsPowerDelivery() {
        return bank.needsPowerDelivery();
    }

    @Override
    public int getStartPhysicalAddress() {
        return bank.getStartPhysicalAddress();
    }

    @Override
    public void setWriteVerification(boolean doReadVerf) {
        bank.setWriteVerification(doReadVerf);
    }

    @Override
    public void read(int startAddr, boolean readContinue, byte[] readBuf, int offset, int len) throws OneWireIOException, OneWireException {
        bank.read(startAddr, readContinue, readBuf, offset, len);
    }

    @Override
    public void write(int startAddr, byte[] writeBuf, int offset, int len) throws OneWireIOException, OneWireException {
        bank.write(startAddr, writeBuf, offset, len);
    }

    /**
     * Display the information about the current memory back provided.
     *
     */
    public String getDetails()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "|------------------------------------------------------------------------\n");
        sb.append("| Bank: (" + bank.getBankDescription() + ")\n");
        sb.append("| Implements : MemoryBank");

        if (bank instanceof PagedMemoryBank)
            sb.append(", PagedMemoryBank");

        sb.append("\n");
        sb.append("| Size " + bank.getSize()
                + " starting at physical address "
                + bank.getStartPhysicalAddress() + "\n");
        sb.append("| Features:");

        if (bank.isReadWrite())
            sb.append(" Read/Write");

        if (bank.isWriteOnce())
            sb.append(" Write-once");

        if (bank.isReadOnly())
            sb.append(" Read-only");

        if (bank.isGeneralPurposeMemory())
            sb.append(" general-purpose");
        else
            sb.append(" not-general-purpose");

        if (bank.isNonVolatile())
            sb.append(" non-volatile");
        else
            sb.append(" volatile");

        if (bank.needsProgramPulse())
            sb.append(" needs-program-pulse");

        if (bank.needsPowerDelivery())
            sb.append(" needs-power-delivery");

        // check if has paged services
        if (bank instanceof PagedMemoryBank)
        {

            // caste to page bank
            PagedMemoryBank pbank = ( PagedMemoryBank ) bank;

            // page info
            sb.append("\n");
            sb.append("| Pages: " + pbank.getNumberPages()
                    + " pages of length ");
            sb.append(pbank.getPageLength() + " bytes ");

            if (bank.isGeneralPurposeMemory())
                sb.append("giving " + pbank.getMaxPacketDataLength()
                        + " bytes Packet data payload");

            if (pbank.hasPageAutoCRC())
            {
                sb.append("\n");
                sb.append("| Page Features: page-device-CRC");
            }

            if (pbank.hasExtraInfo())
            {
                sb.append("\n");
                sb.append("| Extra information for each page:  "
                        + pbank.getExtraInfoDescription()
                        + ", length " + pbank.getExtraInfoLength() + "\n");
            }
            else
                sb.append("\n");
        }
        else
            sb.append("\n");

        sb.append(
                "|------------------------------------------------------------------------\n");
        return sb.toString();
    }

    //////////////////////////////////////////////////////////////
    /**
     * Read a block from a memory bank
     *
     * @param  addr  address to start reading from
     * @param  len   length of data to read
     */
    public byte[] read(int addr, int len) throws OneWireException {

        byte[] read_buf = new byte [len];

        // read the entire bank
        bank.read(addr, false, read_buf, 0, len);
        return read_buf;
    }

    /**
     * Write a block of data with the provided MemoryBank.
     *
     * @param  data  data to write in a byte array
     * @param  addr  address to start the write
     */
    public void write(byte[] data, int addr) throws OneWireException {
        bank.write(addr, data, 0, data.length);
        System.out.println();
        System.out.println("wrote block length " + data.length + " at addr "
                + addr);
    }

    //////////////////////////////////////////////////////////////
    /**
     * Read a block from a memory bank and print in hex
     *
     * @param  addr  address to start reading from
     * @param  len   length of data to read
     */
    public String dumpBankBlock(int addr, int len) throws OneWireException {

        byte[] read_buf = new byte [len];

        // read the entire bank
        bank.read(addr, false, read_buf, 0, len);
        return Utils.hexPrint(read_buf, 0, len);
    }

    /**
     * Write a UDP packet to the specified page in the
     * provided PagedMemoryBank.
     *
     * @param  data  data to write in a byte array
     * @param  pg    page number to write packet to
     */
    public void bankWritePacket (byte[] data, int pg) throws OneWireException {
        ((PagedMemoryBank)bank).writePagePacket(pg, data, 0, data.length);
        System.out.println();
        System.out.println("wrote packet length " + data.length
                + " on page " + pg);
    }
}
