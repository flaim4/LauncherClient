package net.subworld.minecraft;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class AssetIndex {
    @SerializedName("map_to_resources")
    private boolean mapToResources;
    private boolean virtual;
    private Map<String, AssetObject> objects;
    @Getter
    @AllArgsConstructor
    public static class AssetObject {
        private long size;
        private String hash;

        public String getPrefix() {
            return this.getHash().substring(0, 2);
        }
    }
}