package io.gardenerframework.fragrans.messages.resource.loader;

import io.gardenerframework.fragrans.messages.resource.annotation.ResourceFormat;
import io.gardenerframework.fragrans.messages.resource.utils.ResourceUtils;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author ZhangHan
 * @date 2022/6/10 0:15
 */
@ResourceFormat("java.properties")
@Component
public class PropertyFileResourceBundleLoader implements ResourceBundleLoader {
    @Override
    public ResourceBundle load(String baseName, String bundleName, Locale locale, String charset, ClassLoader loader, boolean reload) throws Exception {
        //从父类的控件直接抄过来的。主要是让properties的编码和设置一致
        final String resourceName = ResourceUtils.toResourceName(bundleName, "properties");
        final ClassLoader classLoader = loader;
        final boolean reloadFlag = reload;
        InputStream inputStream;
        try {
            inputStream = AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>) () -> {
                InputStream is = null;
                if (reloadFlag) {
                    URL url = classLoader.getResource(resourceName);
                    if (url != null) {
                        URLConnection connection = url.openConnection();
                        if (connection != null) {
                            connection.setUseCaches(false);
                            is = connection.getInputStream();
                        }
                    }
                } else {
                    is = classLoader.getResourceAsStream(resourceName);
                }
                return is;
            });
        } catch (PrivilegedActionException ex) {
            throw ex.getException();
        }
        if (inputStream != null) {
            try (InputStreamReader bundleReader = new InputStreamReader(inputStream, charset)) {
                return new PropertyResourceBundle(bundleReader);
            }

        } else {
            return null;
        }
    }
}
