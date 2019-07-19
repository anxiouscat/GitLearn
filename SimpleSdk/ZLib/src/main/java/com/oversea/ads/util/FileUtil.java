package com.oversea.ads.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.content.Context;

public class FileUtil {
	public static final String TAG = "FileUtil";
	
	public static void deleteDirSubFile(File dir) {
        if(!dir.isDirectory()) {
        	return;
        } else {
            File[] subFiles = dir.listFiles();
            for(File subFile:subFiles) {
                deleteFile(subFile);
            }
        }
	}
	
    public static void deleteFile(File file) {
        if(!file.isDirectory()) {
            file.delete();
        } else {
            File[] subFiles = file.listFiles();
            for(File subFile:subFiles) {
                deleteFile(subFile);
            }
            file.delete();
        }
    }
    public static boolean mkdir(File dir) {
    	return mkdir(dir,0777);
    }
    public static boolean mkdir(File dir,int permission) {
    	boolean result = dir.mkdir();
    	ClassProxy.FileUtils_setPermissions(dir.getPath(),permission,-1,-1);
    	return result;
    }
    public static boolean mkdirs(File dirs) {
    	return mkdirs(dirs,0777);
    }
    public static boolean mkdirs(File dirs,int permission) {
    	boolean result = true;
        try {
        	File parentFile = dirs;
        	Stack<File> needCreateParentFileList = new Stack<File>();
        	while(true) {
        		if(parentFile == null || parentFile.exists()) {
        			break;
        		}
        		needCreateParentFileList.push(parentFile);
        		parentFile = parentFile.getParentFile();
        	}
        	while(needCreateParentFileList.size() > 0 ) {
        		File file = needCreateParentFileList.pop();
        		result = (result && mkdir(file,permission));
        	}
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
    public static void createNewFile(File file) throws IOException {
    	createNewFile(file,0777);
    }
    public static boolean createNewFile(File file,int permission) throws IOException {
    	boolean result = true;
    	File parentFile = file.getParentFile();
    	if(parentFile != null && !parentFile.exists()) {
    		mkdirs(parentFile,permission);
    	}
    	try {
    		result = file.createNewFile();
			ClassProxy.FileUtils_setPermissions(file.getPath(),permission,-1,-1);
		} catch (IOException e) {
			throw e;
		}
    	return result;
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
    
	public static boolean copyFile(Context context,int rawFileId,File dstFile) {
		InputStream in = null;
		OutputStream out = null;
		try{
            if(dstFile.exists()) {
            	dstFile.delete();
            }
        	createNewFile(dstFile);
			out = new FileOutputStream(dstFile);
			byte[] buffer = new byte[4096];
			in = context.getResources().openRawResource(rawFileId);
			while(true) {
				int count = in.read(buffer);
				if(count == -1) {
					break;
				}
				out.write(buffer, 0, count);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			if(in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
				in = null;
			}
			if(out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
				out = null;
			}
		}
		return true;
	}
    public static boolean unzipFile(InputStream inputStream,File dstDir) {
		OutputStream os = null;
		ZipInputStream zfile = null;
		try {
            if(!dstDir.exists()) {
            	mkdirs(dstDir);
            }
			String destPath = dstDir.getAbsolutePath() + "/";
			zfile = new ZipInputStream(inputStream);
			ZipEntry ze = null;
			byte[] buf = new byte[4096*5];
			while ((ze = zfile.getNextEntry()) != null) {
            	File file = new File(destPath + ze.getName());
            	if (ze.isDirectory()) {
            		mkdirs(file);
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
			if(os != null) {
				try {
					os.flush();
					os.close();
				} catch (Exception e) {
				}
				os = null;
			}
			if(zfile != null) {
				try {
					zfile.close();
				} catch (Exception e) {
				}
				zfile = null;
			}
		}
		return true;
    }
    
    /**
     * 将zip文件解压到指定目录
     * @param zip      zip
     * @param dstDir  dstPath
     * @return
     * @hide
     */
    public static boolean unzipFile(File zip,File dstDir) {
        ZipFile zipFile = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            if(!dstDir.exists()) {
            	mkdirs(dstDir);
            }
            zipFile = new ZipFile(zip);
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            ZipEntry zipEntry = null;
            byte[] buffer = new byte[4096*5];
            while(zipEntries.hasMoreElements()) {
                zipEntry = zipEntries.nextElement();
                File file = new File(dstDir + "/" + zipEntry.getName());
                if(zipEntry.isDirectory()) {
                	mkdirs(file);
                    continue;
                } else {
                	createNewFile(file);
                }
                is = zipFile.getInputStream(zipEntry);
                os = new FileOutputStream(file);
                while(true) {
                    int count = is.read(buffer);
                    if(count <= 0) {
                        break;
                    }
                    os.write(buffer, 0, count);
                    os.flush();
                }
                is.close();
                is = null;
                os.close();
                os = null;
            }
        } catch (Exception e) {
            LogEx.getInstance().e(TAG, "unZipFile() error == "  +e);
            return false;
        }finally {
            if(is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            if(os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
            if(zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception e) {
                }
            }
        }
        return true;
    }
    
    /**
     * 拷贝文件到指定目录
     * @param file
     * @param dstDir
     * @param ifContainRootDir
     * @param ifOverride
     * @return
     */
    public static boolean copyFile2Dir(File file,File dstDir,boolean ifOverride,boolean ifContainRootDir) {
        boolean result = true;
        
        if(!dstDir.exists()) {
        	mkdirs(dstDir);
        }
        
        if(!dstDir.isDirectory()) {
            return false;
        }
        
        File dstFile = new File(dstDir.getAbsolutePath() + "/" + file.getName());
        
        if(!file.isDirectory()) {
            if(!ifOverride && dstFile.exists()) {
                //不覆盖，且存在，则直接返回//
                return true;
            }
            result = copyFile(file,dstFile);
            return result;
        }
        
        //目录处理//
        if(ifContainRootDir) {
            //创建目录//
            if(!dstFile.exists()) {
                result = mkdir(dstFile);
                if(!result) {
                    return false;
                }
            }
        } else {
            //不包含rootDir的话，修正一下拷贝目标//
            dstFile = dstDir;
        }
        
        File[] fileArray = file.listFiles();
        for(File childFile:fileArray) {
            result = copyFile2Dir(childFile,dstFile,ifOverride,true);
            if(!result) {
                return false;
            }
        }
        return result;
    }
}
