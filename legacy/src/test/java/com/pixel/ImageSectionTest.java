package test.java.com.pixel;

import main.java.com.pixel.BufferFragment;
import main.java.com.pixel.util.Rect;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class ImageSectionTest {

    /*public static BufferedImage compile(Main.GlobalData data){
        BufferedImage compiled = new BufferedImage(data.sectionBounds.width, data.sectionBounds.height, data.BUFFER_TYPE);
        for(java.util.List<BufferFragment> list : data.allNodes.values()){
            for(BufferFragment fragment : list){
                fragment.draw(compiled);
            }
        }
        return compiled;
    }

    public record GlobalData(Map<Integer, List<BufferFragment>> allNodes, Rect sectionBounds, int FRAGMENT_HEIGHT, int BUFFER_TYPE) {}

    public static Rect test(Main.GlobalData data, int x, int y){
        compiled = compile(data);
        compiled.setRGB(x, y, Color.BLUE.getRGB());

        BufferFragment orCreate = getOrCreate(x, y, data.FRAGMENT_HEIGHT, data.allNodes, data.sectionBounds);
        fill(orCreate, Color.YELLOW.getRGB());

        compiled = compile(data);
        compiled.setRGB(x, y, Color.BLUE.getRGB());
        return orCreate == null ? null : orCreate.getBounds();
    }

    public static void fill(BufferFragment fragment, int color){
        if(fragment == null) return;
        for(int x = fragment.getBounds().getLeft(); x < fragment.getBounds().getLeft() + fragment.getBounds().width; x++)
            for(int y = fragment.getBounds().getTop(); y < fragment.getBounds().getTop() + fragment.getBounds().height; y++){
                fragment.set(x, y, color);
            }
    }*/
}
