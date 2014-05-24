package com.padule.cospradar.mock;

import java.util.ArrayList;
import java.util.List;

import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.User;
import com.padule.cospradar.util.ImageUtils;

public class MockFactory {

    public static User getUser1() {
        return new User(1, "愛音", "https://dl.dropboxusercontent.com/s/evhzmw6ckqgayq1/aine_1.png");
    }

    public static Charactor getCharactor1(User user) {
        return new Charactor(1, user.getId(), "六合塚 弥生", "PSYCHO-PASS サイコパス", 
                "https://dl.dropboxusercontent.com/s/evhzmw6ckqgayq1/aine_1.png");
    }
    public static Charactor getCharactor2(User user) {
        return new Charactor(2, user.getId(), "赤司 征十郎", "黒子のバスケ", 
                "https://dl.dropboxusercontent.com/s/412w2mf96350ef0/aine_2.png");
    }
    public static Charactor getCharactor3(User user) {
        return new Charactor(3, user.getId(), "名瀬 美月", "境界の彼方", 
                "https://dl.dropboxusercontent.com/s/57a5yuhy45ltmk2/aine_3.png");
    }
    public static Charactor getCharactor4(User user) {
        return new Charactor(4, user.getId(), "園田 海未", "ラブライブ", 
                "https://dl.dropboxusercontent.com/s/gd9gvct1l1tqjc0/aine_4.png");
    }
    public static Charactor getCharactor5(User user) {
        return new Charactor(5, user.getId(), "鬼龍院 皐月", "キルラキル", 
                "https://dl.dropboxusercontent.com/s/r4nrzsp1ifi1c8q/aine_5.png");
    }
    public static Charactor getCharactor6(User user) {
        return new Charactor(6, user.getId(), "ミカサ・アッカーマン", "進撃の巨人", 
                "https://dl.dropboxusercontent.com/s/9385nvtv6zyu2ol/aine_6.png");
    }
    public static Charactor getCharactor7(User user) {
        return new Charactor(7, user.getId(), "セーラー・プルート", "セーラームーン", 
                "https://dl.dropboxusercontent.com/s/vxectzyhtbcxaga/aine_7.png");
    }
    public static Charactor getCharactor8(User user) {
        return new Charactor(8, user.getId(), "初音ミク", "初音ミク", 
                "https://dl.dropboxusercontent.com/s/ayr8jqljg6hiaks/aine_8.png");
    }

    public static List<Charactor> getCharactors() {
        List<Charactor> charactors = new ArrayList<Charactor>();
        User user = getUser1();
        charactors.add(getCharactor1(user));
        charactors.add(getCharactor2(user));
        charactors.add(getCharactor3(user));
        charactors.add(getCharactor4(user));
        charactors.add(getCharactor5(user));
        charactors.add(getCharactor6(user));
        charactors.add(getCharactor7(user));
        charactors.add(getCharactor8(user));

        return charactors;
    }

    public static Charactor createCharactor(String name, String title, String localImage) {
        return new Charactor(1, getUser1().getId(), name, title, ImageUtils.convertToValidUrl(localImage));
    }

}
