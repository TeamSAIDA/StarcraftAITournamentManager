package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtils 
{

	public static void writeToFile(String data, String filename)
	{
		try
		{
			File file = new File(filename);
			if (!file.exists())
			{
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.close();
			fw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private static void DeleteRecursive(File f) 
	{
		try
		{
			if (f.isDirectory()) 
			{
				for (File c : f.listFiles())
				{
					DeleteRecursive(c);
				}
			}
			
			f.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void CleanDirectory(File f) 
	{
		try
		{
			for (File c : f.listFiles())
			{
				DeleteRecursive(c);
			}
		}
		catch (Exception e)
		{
			
		}
	}
	
	public static void CreateDirectory(String dir)
	{
		File f = new File(dir); 
		if (!f.exists())
		{
			f.mkdirs();
		}
	}
	
	public static void DeleteDirectory(File f) 
	{
		System.out.println("Deleting directory:" + f.getAbsolutePath());
		DeleteRecursive(f);
	}
	
	public static void DeleteFile(File f)
	{
		DeleteRecursive(f);
	}
	
	public static void CopyFile(File source, File dest)
	{
		try
		{
			copy(new FileInputStream(source), new FileOutputStream(dest));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void CopyDirectory(String source, String dest)
	{
		try
		{
			File sourceDir = new File(source);
			File destDir = new File(dest);	
			destDir.mkdirs();
			
			Path destPath = destDir.toPath();
			for (File sourceFile : sourceDir.listFiles()) 
			{
			    Path sourcePath = sourceFile.toPath();
			    File newFile = new File(sourcePath.toString());
			    
			    if (newFile.isDirectory())
			    {
			    	newFile.mkdirs();
			    	CopyDirectory(sourceFile.toString(), destPath.resolve(sourcePath.getFileName()).toString());
			    }
			    else
			    {
			    	Files.copy(sourcePath, destPath.resolve(sourcePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
			    }
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void CopyFilesInDirectory(File source, File dest)
	{
		try
		{
			copy(new FileInputStream(source), new FileOutputStream(dest));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void copy(InputStream in, OutputStream out) 
	{
		try
		{
			byte[] buffer = new byte[1024];
			while (true) 
			{
				int readCount = in.read(buffer);
				if (readCount < 0) 
				{
					break;
				}
				out.write(buffer, 0, readCount);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
