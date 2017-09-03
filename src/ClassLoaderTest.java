import java.io.FileInputStream;
import java.io.IOException;

public class ClassLoaderTest {
	public static void main(String[] args) throws Exception {
		ClassLoader cl = ClassLoaderTest.class.getClassLoader();
		ClassLoader pcl = cl.getParent();
		ClassLoader ppcl = pcl.getParent();
		p("ClassLoaderTest类的ClassLoader是: " + cl.getClass().getName());
		p("ClassLoaderTest类的Parent ClassLoader是: " + pcl.getClass().getName());
		p("ClassLoaderTest类的Parent ClassLoader的Parent ClassLoader是: " + ppcl);

		pNewLine();

		ClassLoader scl = ClassLoader.getSystemClassLoader();
		p("系统ClassLoader是: " + scl.getClass().getName());

		pNewLine();
		
		ClassLoader strcl = String.class.getClassLoader();
		p("String类的ClassLoader是: " + strcl + ", 此处说明String是由Bootstrp ClassLoader加载的");
		
		pNewLine();
		
		Class<?> cls = scl.loadClass("com.sun.nio.zipfs.ZipCoder");
		p("用系统ClassLoader加载%JAVA_HOME%/jre/lib/ext下的类: " + cls);
		p("但是取得ZipCoder类的ClassLoader是: " + cls.getClassLoader().getClass().getName() + ",说明ZipCoder类并不是由系统ClassLoader加载的，而是已经在JVM启动时由ExtClassLoader加载过。");
		
		pNewLine();
		
		MyClassLoader mcl = new MyClassLoader();
		Class<?> tstcls = mcl.loadClass("com.hxj.test.Test");
		p("用自定义ClassLoader加载类: " + tstcls.getName());
		p("Test的ClassLoader是: " + tstcls.getClassLoader().getClass().getName());
		Class<?> mycls = mcl.loadClass("com.hxj.MyCls");
		p("用自定义ClassLoader加载类: " + mycls.getName());
		p("MyCls的ClassLoader是: " + mycls.getClassLoader().getClass().getName());
		p("调用MyCls方法:");
		Object obj = mycls.newInstance();
		mycls.getMethod("hi").invoke(obj);
		
		pNewLine();
		
		p("===END===");
	}

	private static void p(String str) {
		System.out.println(str);
	}

	private static void pNewLine() {
		System.out.println();
	}
	
	public static class MyClassLoader extends ClassLoader {

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			if ("com.hxj.MyCls".equals(name)) {
				byte[] data = new byte[1024];
				int len;
				FileInputStream fis = null;
				try {
					fis = new FileInputStream("C:/Users/Hxuejie/Desktop/MyCls.class");
					len = fis.read(data);
					Class<?> dcls = defineClass(name, data, 0, len);
					return dcls;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return super.findClass(name);
		}
	}
}
