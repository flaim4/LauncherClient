package net.subworld.minecraft;


import com.google.common.eventbus.Subscribe;
import net.subworld.Main;
import net.subworld.Settings;
import net.subworld.events.SettingsUpdateEvent;

public class InstanceMannager {
    public InstanceMannager(Settings settings) {
        Main.bus.register(this);
    }

    @Subscribe
    public void onSettingsUpdate(SettingsUpdateEvent event) {

    }
}
