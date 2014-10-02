package sweethome;

import com.dalsemi.onewire.OneWireAccessProvider;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.adapter.NetAdapterHost;
import org.apache.log4j.Logger;

/**
 * Created by andrey on 30.09.2014.
 */
public class OneWireServer implements Runnable{
    private Logger log = Logger.getLogger(this.getClass());
    private NetAdapterHost netAdapter;
    private DSPortAdapter adapter;

    @Override
    public void run() {
        boolean isErrorLogged = false;
        for(;;){
            try {
                // init one wire adapter
                adapter = OneWireAccessProvider.getDefaultAdapter();
                netAdapter = new NetAdapterHost(adapter, true);
                Thread thread = new Thread(netAdapter);
                thread.start();
                log.info("The following adapter was connected: " + adapter.getAdapterName() + ", port: " + adapter.getPortName());
                isErrorLogged = false;
                break;
            } catch (Exception e) {
                // log error if it wasn't logged before
                if( !isErrorLogged ) {
                    log.error("Cannot find any 1-Wire adapters during startup. " + e);
                    isErrorLogged = true;
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    break;
                }
            }
        }
    }

    /** clean up before exiting program */
    public void cleanup()
    {

        if(netAdapter != null) {
            netAdapter.stopHost();
        }

        try
        {
            if (adapter != null)
            {
                adapter.endExclusive(); // end exclusive use of adapter
                adapter.freePort();     // free port used by adapter
            }
        }
        catch (Exception e)
        {
            log.error("Cannot cleanup 1-Wire adapter", e);
        }

        return;
    }
}
