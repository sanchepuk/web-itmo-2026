package ru.itmo.wp.util;

import com.google.gson.Gson;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

public class ServletUtils {
    private static String appRoot = "src/main/webapp/static";

    public static File getFile(String uri, ServletContext context){
        File file = Paths.get(appRoot, uri).toFile();
        if (!file.isFile()) {
            file = new File(context.getRealPath("/static" + uri));
        }
        return file;
    }

    public static boolean hasUriDots(String uri, HttpServletResponse response) throws IOException {
        if (uri.contains("../")){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return true;
        }
        return false;
    }

    public static <T> void sendObjectGSON(T object, PrintWriter writer){
        String json = new Gson().toJson(object);
        writer.print(json);
        writer.flush();
    }
}
