package net.subworld.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.subworld.Settings;

@Getter
@AllArgsConstructor
public class SettingsUpdateEvent extends Event {
    private Settings settings;
}
