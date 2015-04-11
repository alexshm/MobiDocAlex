package example.com.mobidoc;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * class that reads the properties file
 */
public class ConfigReader {
    private String FileName;
    private Context context;

    private Properties properties;

    public ConfigReader(Context context) {

        this.context = context;
        // new properties obj
        properties = new Properties();
        FileName="config.properties";
    }

    /**
     * getAssets() Return an AssetManager instance for your
     * application's package. AssetManager Provides access to an
     * application's raw asset files;
     */
    public Properties getProperties() {

        try {
            //* Open an asset using ACCESS_STREAMING mode. This
            AssetManager assetManager = context.getResources().getAssets();
            String[] files = assetManager.list("Files");
            //Loads properties from the specified InputStream,
            InputStream inputStream = assetManager.open(FileName);

            properties.load(inputStream);

        } catch (IOException e) {

            Log.e("ConfigReader", "error while reading : "+e.toString());
            return null;
        }
        return properties;
    }
}
