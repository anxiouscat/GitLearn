package com.oversea.ads.easyio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {
    public static void deleteDirSubFile(File dir) {
        if (!dir.isDirectory()) {
            return;
        } else {
            File[] subFiles = dir.listFiles();
            for (File subFile : subFiles) {
                deleteFile(subFile);
            }
        }
    }

    public static void deleteFile(File file) {
        if (!file.isDirectory()) {
            file.delete();
        } else if (file.exists()){
            File[] subFiles = file.listFiles();
            for (File subFile : subFiles) {
                deleteFile(subFile);
            }
            file.delete();
        }
    }

    public static void createNewFile(File file) {
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
        }
    }

    public static boolean unzipFile(InputStream inputStream, File dstDir) {
        OutputStream os = null;
        ZipInputStream zfile = null;
        try {
            if (!dstDir.exists()) {
                dstDir.mkdirs();
            }
            String destPath = dstDir.getAbsolutePath() + "/";
            zfile = new ZipInputStream(inputStream);
            ZipEntry ze = null;
            byte[] buf = new byte[4096 * 8];
            while ((ze = zfile.getNextEntry()) != null) {
                File file = new File(destPath + ze.getName());
                if (ze.isDirectory()) {
                    file.mkdirs();
                    continue;
                } else {
                    createNewFile(file);
                }
                os = new FileOutputStream(file);
                int readLen = 0;
                while ((readLen = zfile.read(buf, 0, 4096)) != -1) {
                    os.write(buf, 0, readLen);
                }
                os.flush();
                os.close();
                os = null;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (Exception e) {
                }
                os = null;
            }
            if (zfile != null) {
                try {
                    zfile.close();
                } catch (Exception e) {
                }
                zfile = null;
            }
        }
        return true;
    }

    public static boolean unzipFile(File zip, File dstDir) {
        try {
            return unzipFile(new FileInputStream(zip), dstDir);
        } catch (FileNotFoundException e) {
        }
        return false;
    }

    public static String getFileMD5Digest(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;

        try {
            in = new FileInputStream(file);
            digest = MessageDigest.getInstance("MD5");

            byte buffer[] = new byte[1024];
            int len;
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
            in = null;
        } catch (Exception e) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }

        if (digest != null) {
            BigInteger bigInt = new BigInteger(1, digest.digest());
            String bigIntString = bigInt.toString(16);
            while (bigIntString.length() < 32) {
                bigIntString = "0" + bigIntString;
            }
            return bigIntString;
        }
        return null;
    }

    public static boolean checkFileMD5(File file, String md5) {
        if (file == null || !file.exists()) {
            return false;
        }

        if (md5 == null) {
            return true;
        }

        String filemd5 = FileUtil.getFileMD5Digest(file);
        if (filemd5 == null) {
            return false;
        }

        if (filemd5.length() == md5.length()) {
            return filemd5.equals(md5);
        } else {
            int flen = filemd5.length();
            int len = md5.length();
            if (flen < len) {
                int cnt = len - flen;
                for (int i = 0; i < cnt; i++) {
                    filemd5 = "0" + filemd5;
                }

                return filemd5.equals(md5);
            }
        }

        return false;
    }

    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = true;
        //确保文件夹及文件存在//
        try {
            if(destFile.exists()) {
                destFile.delete();
            }
            createNewFile(destFile);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
            return result;
        }

        //开始拷贝数据//
        FileChannel in = null;
        FileChannel out = null;
        try {
            in = new FileInputStream(srcFile).getChannel();
            out = new FileOutputStream(destFile).getChannel();
            long count = in.transferTo(0, in.size(), out);
            if(count != in.size()) {
                result = false;
                return result;
            }
            out.force(true);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
            return result;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
            try {
                out.close();
            } catch (Exception e) {
            }
        }
        return result;
    }

    public static boolean sAsyncUnZipSuccessed = false;
    public static boolean unzipFileAsync(final InputStream inputStream, final File dstDir) {
        sAsyncUnZipSuccessed = false;
        final Object obj = new Object();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream os = null;
                ZipInputStream zfile = null;
                try {
                    if (!dstDir.exists()) {
                        dstDir.mkdir();
                    }
                    String destPath = dstDir.getAbsolutePath() + "/";
                    zfile = new ZipInputStream(inputStream);
                    ZipEntry ze = null;
                    byte[] buf = new byte[1024 * 8];
                    while ((ze = zfile.getNextEntry()) != null) {
                        String name = ze.getName();
                        if (name.contains("..")) {
                            sAsyncUnZipSuccessed = false;
                            return;
                        }
                        File file = new File(destPath + name);
                        if (ze.isDirectory()) {
                            file.mkdir();
                            continue;
                        } else {
                            createNewFile(file);
                        }
                        os = new FileOutputStream(file);
                        int readLen = 0;
                        while (true) {
                            readLen = zfile.read(buf, 0, buf.length);
                            if (readLen == 0) {
                                readLen = zfile.read(buf, 0, buf.length);
                            }

                            if (readLen < 0) {
                                break;
                            } else if (readLen == 0) {
                                os.flush();
                                os.close();
                                os = null;

                                //by bitao 20170406
                                //若zip文件损坏，调用zfile.close()可能会ANR，因此不要调用了，
                                //此处测试过不会有内存泄漏.
                                zfile = null;

                                throw new IOException("can't read when unzip inputstream");
                            }

                            os.write(buf, 0, readLen);
                        }
                        os.flush();
                        os.close();
                        os = null;
                    }

                    sAsyncUnZipSuccessed = true;
                } catch (Exception e) {
                    sAsyncUnZipSuccessed = false;
                } finally {
                    if (os != null) {
                        try {
                            os.flush();
                            os.close();
                        } catch (Exception e) {
                        }
                        os = null;
                    }
                    if (zfile != null) {
                        try {
                            zfile.close();
                        } catch (Exception e) {
                        }
                        zfile = null;
                    }

                    synchronized (obj) {
                        obj.notify();
                    }
                }
            }
        }, "unzipthread");
        thread.setPriority(Thread.NORM_PRIORITY + 2);
        thread.start();

        try {
            synchronized (obj) {
                obj.wait(4000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            sAsyncUnZipSuccessed = false;
        }
        return sAsyncUnZipSuccessed;
    }
}
