package net.subworld.res;

import lombok.Getter;

import java.nio.file.Path;

@Getter
public class ResourceMannager {
    private final Path packs;
    public ResourceMannager(Path packs) {
        this.packs = packs;
    }


}
