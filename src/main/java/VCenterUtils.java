import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.mo.*;

import java.net.URL;

public class VCenterUtils {
  private final String myHost;
  private final String myUser;
  private final String myPassword;

  public VCenterUtils(String host, String user, String password) {
    this.myHost = host;
    myUser = user;
    myPassword = password;
  }

  public String getMachineNameByMac(String mac) throws Exception {
    ServiceInstance si = new ServiceInstance(new URL("https://" + myHost + "/sdk"), myUser, myPassword, true);

    try {
      final VirtualMachine vm = getMachineByMac(si, mac);
      if (vm == null)
        return null;

      return vm.getName();
    } finally {
      si.getServerConnection().logout();
    }
  }

  private static VirtualMachine getMachineByMac(ServiceInstance si, String mac) throws Exception {
    Folder rootFolder = si.getRootFolder();

    ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
    if (mes == null || mes.length == 0)
      return null;

    for (ManagedEntity managedEntity : mes) {
      VirtualMachine vm = (VirtualMachine) managedEntity;

      for (VirtualDevice device : vm.getConfig().getHardware().getDevice()) {
        if (!(device instanceof VirtualEthernetCard))
          continue;

        VirtualEthernetCard card = (VirtualEthernetCard) device;

        if (card.getMacAddress().equalsIgnoreCase(mac))
          return vm;
      }
    }

    return null;
  }

}
