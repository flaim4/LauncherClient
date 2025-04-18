package net.subworld.minecraft;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.*;
import net.subworld.OperatingSystem;
import net.subworld.json.AlwaysListTypeAdapterFactory;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Version {
    private AssetIndex assetIndex;
    private JavaVersion javaVersion;
    private String id;
    private String mainClass;
    private VersionType type;
    private OffsetDateTime releaseTime;
    private OffsetDateTime time;
    private String minecraftArguments;
    private String assets;
    private int complianceLevel;
    private EnumMap<DownloadType, Download> downloads;
    private List<Library> libraries;
    private EnumMap<ArgumentType, List<Argument>> arguments;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class AssetIndex {
        private String id;
        private String sha1;
        private long size;
        private long totalSize;
        private String url;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class JavaVersion {
        private String component;
        private int majorVersion;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Download {
        private String sha1;
        private long size;
        private String url;
    }

    @Getter
    public enum ArgumentType {
        JVM("jvm"),
        GAME("game");

        private static final Map<String, ArgumentType> lookup = new HashMap<>();

        static {
            for (ArgumentType type : ArgumentType.values()) {
                lookup.put(type.getJsonName(), type);
            }
        }

        public static ArgumentType getByName(String jsonName) {
            ArgumentType type = lookup.get(jsonName);

            if (type == null) {
                throw new IllegalArgumentException("jsonName: " + jsonName);
            }

            return type;
        }

        private final String jsonName;

        ArgumentType(String jsonName) {
            this.jsonName = jsonName;
        }

    }

    @Getter
    public enum DownloadType {
        CLIENT("client"),
        CLIENT_MAPPINGS("client_mappings"),
        SERVER("server"),
        SERVER_MAPPINGS("server_mappings"),
        WINDOWS_SERVER("windows_server");

        private static final Map<String, DownloadType> lookup = new HashMap<>();

        static {
            for (DownloadType type : DownloadType.values()) {
                lookup.put(type.getJsonName(), type);
            }
        }

        public static DownloadType getByName(String name) {
            DownloadType type = lookup.get(name);

            if (type == null) {
                throw new IllegalArgumentException(name);
            }

            return type;
        }

        private final String jsonName;

        DownloadType(String jsonName) {
            this.jsonName = jsonName;
        }

    }

    @Getter
    public enum VersionType {
        RELEASE("Release", "release"),
        SNAPSHOT("Snapshot", "snapshot"),
        OLD_BETA("Beta", "old_beta"),
        OLD_ALPHA("Alpha", "old_alpha");

        private static final Map<String, VersionType> lookup = new HashMap<>();

        static {
            for (VersionType type : VersionType.values()) {
                lookup.put(type.getJsonName(), type);
            }
        }

        public static VersionType getByName(String jsonName) {
            VersionType type = lookup.get(jsonName);

            if (type == null) {
                throw new IllegalArgumentException("jsonName: " + jsonName);
            }

            return type;
        }

        private final String readableName;
        private final String jsonName;

        VersionType(String readableName, String jsonName) {
            this.readableName = readableName;
            this.jsonName = jsonName;
        }

        @Override
        public String toString() {
            return this.getReadableName();
        }

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Library implements Ruleable {
        private String name;
        private DownloadList downloads;
        private List<Rule> rules;
        private Map<String, String> natives;
        private ExtractRules extract;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Artifact {
            private String path;
            private String sha1;
            private long size;
            private String url;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DownloadList {
            private Artifact artifact;
            private Map<String, Artifact> classifiers;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Argument implements Ruleable {
        @JsonAdapter(AlwaysListTypeAdapterFactory.class)
        private List<String> value;
        private List<Rule> rules;

        public static Argument withValues(String... values) {
            Argument argument = new Argument();
            argument.value = Arrays.asList(values);
            argument.rules = new ArrayList<>();
            return argument;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        @SerializedName("action")
        private Action action;

        @SerializedName("os")
        private OperatingSystemFilter operatingSystem;

        @SerializedName("features")
        private FeaturesFilter features;

        @Getter
        public enum Action {
            ALLOW("allow"),
            DISALLOW("disallow");

            private static final Map<String, Action> lookup = new HashMap<>();

            static {
                for (Action type : Action.values()) {
                    lookup.put(type.getJsonName(), type);
                }
            }

            public static Action getByName(String jsonName) {
                Action type = lookup.get(jsonName);

                if (type == null) {
                    throw new IllegalArgumentException("jsonName: " + jsonName);
                }

                return type;
            }

            private final String jsonName;

            Action(String jsonName) {
                this.jsonName = jsonName;
            }

        }
    }

    @Getter
    @AllArgsConstructor
    public static class OperatingSystemFilter {
        private String name;
        private String version;
        private String arch;
    }

    @Getter
    @AllArgsConstructor
    public static class FeaturesFilter {
        @SerializedName("is_demo")
        private boolean isDemo;

        @SerializedName("has_custom_resolution")
        private boolean hasCustomResolution;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtractRules {
        private List<String> exclude;
    }

    public interface Ruleable {
        List<Rule> getRules();

        default boolean applyOnThisPlatform() {
            List<Rule> rules = this.getRules();

            Rule.Action lastAction = Rule.Action.DISALLOW;
            if (rules == null || rules.isEmpty()) {
                lastAction = Rule.Action.ALLOW;
            } else {
                for (Rule rule : rules) {
                    OperatingSystemFilter os = rule.getOperatingSystem();
                    if (os == null) {
                        lastAction = rule.getAction();
                    } else {
                        boolean versionMatches = os.getVersion() != null &&
                                Pattern.compile(os.getVersion()).matcher(OperatingSystem.getVersion()).matches();
                        if (versionMatches || OperatingSystem.getArch().equals(os.getArch())) {
                            lastAction = rule.getAction();
                        }
                    }
                }
            }

            return lastAction == Rule.Action.ALLOW;
        }
    }
}