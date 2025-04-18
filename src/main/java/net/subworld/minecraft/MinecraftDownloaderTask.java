package net.subworld.minecraft;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import net.subworld.OperatingSystem;
import net.subworld.exec.Task;
import org.apache.commons.io.file.PathUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

@AllArgsConstructor
public class MinecraftDownloaderTask extends Task<Object> {
    private static final Duration MANIFEST_UPDATE_INTERVAL = Duration.ofHours(12);
    private final Path versionsDir;
    private final Path assetsDir;
    private final Path librariesDir;
    private final Path nativesDir;
    private final Path runtimesDir;
    private final Path instanceResourcesDir;
    private final boolean downloadJava;
    private final Version version;
    private final Gson GSON = new Gson();

    @Override
    public Object execute() throws Exception {
        Version.Download client = version.getDownloads().get(Version.DownloadType.CLIENT);
        Path jarFile = this.versionsDir.resolve(version.getId()).resolve(version.getId() + ".jar");

        if (Files.exists(jarFile) && Files.size(jarFile) == client.getSize()) {
            return version;
        }

        HttpClient client1 = HttpClients.custom()
                .setMaxConnTotal(8)
                .build();

        {
            HttpResponse response = client1.execute(new HttpGet(client.getUrl()));

            try (OutputStream stream = Files.newOutputStream(jarFile)) {
                response.getEntity().writeTo(stream);
            }
        }

        {
            List<Version.Library> nativeLibraries = new ArrayList<>();

            for (Version.Library library : version.getLibraries()) {
                if (!library.applyOnThisPlatform()) {
                    continue;
                }

                if (!OperatingSystem.isArm()) {
                    if (OperatingSystem.is64Bit() && library.getName().endsWith("arm64")) {
                        continue;
                    } else if (library.getName().endsWith("x86")) {
                        continue;
                    }
                }

                Version.Library.DownloadList downloads = library.getDownloads();
                Version.Library.Artifact artifact = downloads.getArtifact();

                if (artifact != null) {
                    Path jarFile2 = this.librariesDir.resolve(artifact.getPath());

                    if (!Files.exists(jarFile2) || Files.size(jarFile2) != artifact.getSize()) {
                        HttpResponse response = client1.execute(new HttpGet(artifact.getUrl()));

                        try (OutputStream stream = Files.newOutputStream(jarFile2)) {
                            response.getEntity().writeTo(stream);
                        }
                    }
                }

                Version.Library.Artifact classifier = this.getClassifier(library);
                if (classifier != null) {
                    nativeLibraries.add(library);
                    Path filePath = this.librariesDir.resolve(classifier.getPath());

                    if (!Files.exists(filePath) || Files.size(filePath) != classifier.getSize()) {
                        HttpResponse response = client1.execute(new HttpGet(classifier.getUrl()));

                        try (OutputStream stream = Files.newOutputStream(filePath)) {
                            response.getEntity().writeTo(stream);
                        }
                    }
                }
            }

            {
                for (Version.Library library : nativeLibraries) {
                    Version.Library.Artifact classifier = this.getClassifier(library);
                    if (classifier == null) {
                        continue;
                    }

                    Path extractPath = Paths.get(this.nativesDir.normalize().toAbsolutePath().toString());
                    Path path = this.librariesDir.resolve(classifier.getPath()).toAbsolutePath();

                    try (ZipFile zipFile = new ZipFile(path.toFile())) {
                        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
                        while (entries.hasMoreElements()) {
                            ZipArchiveEntry entry = entries.nextElement();
                            String name = entry.getName();

                            if (excludeFromExtract(library, name)) {
                                continue;
                            }

                            Path target = extractPath.resolve(name);
                            if (Files.exists(target)) {
                                continue;
                            }

                            if (entry.isDirectory()) {
                                Files.createDirectories(target);
                                continue;
                            } else {
                                Files.createDirectories(target.getParent());
                            }

                            try (InputStream in = zipFile.getInputStream(entry);
                                 OutputStream out = Files.newOutputStream(target)) {
                                byte[] buffer = new byte[4096];
                                int len;
                                while ((len = in.read(buffer)) != -1) {
                                    out.write(buffer, 0, len);
                                }
                            }
                        }
                    }
                }
            }
            downloadAssets(client1);
        }



        return null;
    }

    private void downloadAssets(HttpClient client) throws IOException {
        Version.AssetIndex vAssetIndex = version.getAssetIndex();

        if (vAssetIndex == null) {
            return;
        }

        Path assetsIndexFile = this.assetsDir.resolve("indexes").resolve(vAssetIndex.getId() + ".json");
        if (!Files.exists(assetsIndexFile)) {
            HttpResponse response = client.execute(new HttpGet(vAssetIndex.getUrl()));

            try (OutputStream stream = Files.newOutputStream(assetsIndexFile)) {
                response.getEntity().writeTo(stream);
            }
        }

        AssetIndex assetIndex = GSON.fromJson(new FileReader(assetsIndexFile.toFile()), AssetIndex.class);

        for (Map.Entry<String, AssetIndex.AssetObject> entry : assetIndex.getObjects().entrySet()) {
            String fileName = entry.getKey();
            AssetIndex.AssetObject assetObject = entry.getValue();

            HttpResponse response = client.execute(new HttpGet("https://resources.download.minecraft.net/" + assetObject.getPrefix() + "/" + assetObject.getHash()));

            Path saveAs;
            Path copyTo = null;

            if (assetIndex.isMapToResources()) {
                saveAs = this.instanceResourcesDir.resolve(fileName);

                Path resourcesFile = this.assetsDir.resolve("resources").resolve(fileName);

                if (!Files.exists(saveAs)) {
                    if (Files.exists(resourcesFile) &&
                            Files.size(resourcesFile) == assetObject.getSize() &&
                            sha1(resourcesFile).equals(assetObject.getHash())) {

                        PathUtils.createParentDirectories(saveAs.getParent());
                        Files.copy(resourcesFile, saveAs, StandardCopyOption.REPLACE_EXISTING);

                        continue;
                    } else {
                        copyTo = saveAs;
                        saveAs = resourcesFile;
                    }
                } else {
                    if (Files.size(saveAs) == assetObject.getSize() && sha1(saveAs).equals(assetObject.getHash())) {
                        continue;
                    } else {
                        copyTo = saveAs;
                        saveAs = resourcesFile;
                    }
                }
            } else if (assetIndex.isVirtual()) {
                saveAs = this.assetsDir.resolve("virtual").resolve(vAssetIndex.getId()).resolve(fileName);
            } else {
                saveAs = this.assetsDir.resolve("objects").resolve(assetObject.getPrefix()).resolve(assetObject.getHash());
            }
            PathUtils.createParentDirectories(saveAs.getParent());


            try (OutputStream stream = Files.newOutputStream(saveAs)) {
                response.getEntity().writeTo(stream);
            }
            if (copyTo != null) {
                PathUtils.createParentDirectories(copyTo.getParent());
                Files.copy(saveAs, copyTo, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private boolean excludeFromExtract(Version.Library library, String fileName) {
        if (library.getExtract() == null) {
            return false;
        }

        for (String excludeName : library.getExtract().getExclude()) {
            if (fileName.startsWith(excludeName)) {
                return true;
            }
        }

        return false;
    }

    private Version.Library.Artifact getClassifier(Version.Library library) {
        Map<String, Version.Library.Artifact> classifiers = library.getDownloads().getClassifiers();

        if (classifiers != null) {
            String key = "natives-" + getNativeName();
            Version.Library.Artifact classifier = classifiers.get(key);

            if (classifier == null) {
                classifier = classifiers.get(key + "-" + getQualifierOS());
            }

            return classifier;
        }

        return null;
    }
    public String getNativeName() {
        switch (OperatingSystem.getCurrent()) {
            case WINDOWS:
                return "windows";
            case LINUX:
                return "linux";
            case MACOS:
                return "osx";
            case UNKNOWN:
                throw new RuntimeException("Unsupported OS: " + OperatingSystem.getName());
            default:
                throw new RuntimeException("Unreachable");
        }
    }

    private static String getQualifierOS() {
        if (OperatingSystem.isArm()) {
            if (OperatingSystem.is64Bit()) {
                return "arm64";
            } else {
                return "arm32";
            }
        } else {
            return OperatingSystem.getBits();
        }
    }

    public static String sha1(Path file) throws IOException {
        try (InputStream inputStream = Files.newInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            byte[] buffer = new byte[4096];

            int numRead;
            while ((numRead = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, numRead);
            }

            byte[] mdBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : mdBytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException ex) {
            throw new IOException("SHA-1 algorithm is not available in your JRE", ex);
        }
    }
}
