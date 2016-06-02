package roclas.servers.servlet1;

/**
 * Created by carlos on 02/06/16.
 */

import java.io.IOException;

public class StaticResourceProcessor {
    public void process(Request request, Response response) {
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}