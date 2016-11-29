package com.telemarket.telemarketer.services;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.requests.MimeData;
import com.telemarket.telemarketer.http.requests.Request;
import com.telemarket.telemarketer.http.responses.FileResponse;
import com.telemarket.telemarketer.http.responses.Response;
import com.telemarket.telemarketer.mvc.annotation.Path;
import com.telemarket.telemarketer.mvc.annotation.Service;
import com.telemarket.telemarketer.util.PropertiesHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Be careful!
 * Created by hason on 15/9/30.
 */

@Service
public class SearchService {

    @Path("/search")
    public Response service(Request request) {
        if (request.mimeContainKey("photo")) {
            MimeData photo = request.mimeValue("photo");
            byte[] data = photo.getData();
            try {
                BufferedImage read = ImageIO.read(new ByteInputStream(data, data.length));
                if (read == null) {
                    return new Response(Status.BAD_REQUEST_400);
                }
                showImage(read);
            } catch (IOException e) {
                return new Response(Status.BAD_REQUEST_400);
            }
        } else {
            return new Response(Status.BAD_REQUEST_400);
        }
        return new FileResponse(Status.SUCCESS_200, PropertiesHelper.getTemplateFile("search.html"));
    }

    public static void showImage(BufferedImage image) {
        ImageIcon ic = new ImageIcon(image);
        JFrame world = new JFrame("World");
        JLabel jLabel = new JLabel(ic);
        Panel panel = new Panel();
        panel.add(jLabel);
        world.setContentPane(panel);
        world.setSize(image.getWidth(), image.getHeight());
        world.setVisible(true);
    }

}
