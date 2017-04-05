package txy.galleryhelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanxinye on 2017/4/1.
 */
public final class FolderEntity {

    private int num;
    private String name;
    private List<String> paths = new ArrayList<>();

    public int getNum() {
        return paths.size();
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setName(String name) {
        this.name = name;
    }
}
