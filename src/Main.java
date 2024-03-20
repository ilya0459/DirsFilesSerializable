import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static final String FILE_NAME1 = "save1.dat";
    public static final String FILE_NAME2 = "save2.dat";
    public static final String FILE_NAME3 = "save3.dat";
    public static final String DIR_NAME = "C://Games//savegames//GunRunner//";
    public static final String NEW_DIR_NAME = "C://Games//res//";
    public static final String ZIP_NAME = "C://Games//savegames//GunRunner//zip.zip";


    public static void main(String[] args) {
        GameProgress gameProgress1 = new GameProgress(10, 20, 3, 3.5);
        GameProgress gameProgress2 = new GameProgress(34, 24, 8, 8.5);
        GameProgress gameProgress3 = new GameProgress(110, 50, 1, 2.9);

        File dirGunRunner = new File("C://Games//savegames", "GunRunner");
        dirGunRunner.mkdir();

        String saveAdress1 = DIR_NAME + FILE_NAME1;
        String saveAdress2 = DIR_NAME + FILE_NAME2;
        String saveAdress3 = DIR_NAME + FILE_NAME3;

        List<String> listFiles = new ArrayList();
        listFiles.add(saveAdress1);
        listFiles.add(saveAdress2);
        listFiles.add(saveAdress3);

        saveGame(saveAdress1, gameProgress1);
        saveGame(saveAdress2, gameProgress2);
        saveGame(saveAdress3, gameProgress3);

        zipFiles(ZIP_NAME, listFiles);   //создание ZIP- архива в папке - "savegames"

        fileDelete(listFiles.get(0));
        fileDelete(listFiles.get(1));
        fileDelete(listFiles.get(2));

        openZip(ZIP_NAME, NEW_DIR_NAME);  // распаковка в другой папке - "res"
        System.out.println(openProgress(NEW_DIR_NAME + FILE_NAME2));
    }


    public static void fileDelete(String fail) {
        Path path = Paths.get(fail);
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void saveGame(String failName, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(failName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(gameProgress);
        } catch (IOException e) {
            System.out.println("1 " + e.getMessage());
        }
    }


    public static void zipFiles(String zipFail, List<String> list) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFail))) {
            for (String listFile1 : list) {
                try (FileInputStream fis = new FileInputStream(listFile1)) {
                    ZipEntry entry = new ZipEntry(listFile1);
                    zos.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zos.write(buffer);
                    zos.closeEntry();
                } catch (Exception ex) {
                    System.out.println("2 " + ex.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("3 " + e.getMessage());
        } catch (IOException ex) {
            System.out.println("4 " + ex.getMessage());
        }

    }


    public static void openZip(String zipFail, String dirName) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFail))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File names = new File(zipEntry.getName());
                FileOutputStream fout = new FileOutputStream(dirName + names.getName());
                for (int i = zis.read(); i != -1; i = zis.read()) {
                    fout.write(i);
                }
                fout.flush();
                zis.closeEntry();
                fout.close();
            }
        } catch (IOException e) {
            System.out.println("5 " + e.getMessage());
        }
    }


    public static GameProgress openProgress(String fail) {
        GameProgress gameProgressSave = null;

        try (FileInputStream fis = new FileInputStream(fail);
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object o = ois.readObject();
            if (o instanceof GameProgress gameProgress) {
                gameProgressSave = gameProgress;
            }
        } catch (FileNotFoundException e) {
            System.out.println("6" + e.getMessage());
        } catch (IOException e) {
            System.out.println("7" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("8" + e.getMessage());
        }

        return gameProgressSave;
    }
}