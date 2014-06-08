package com.padule.cospradar.mock;

import java.util.ArrayList;
import java.util.List;

import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorComment;
import com.padule.cospradar.data.CharactorLocation;
import com.padule.cospradar.data.User;
import com.padule.cospradar.util.ImageUtils;

public class MockFactory {

    public static User getUser1() {
        return new User(1, "愛音", "https://dl.dropboxusercontent.com/s/evhzmw6ckqgayq1/aine_1.png");
    }

    public static Charactor getCharactor1(User user) {
        Charactor charactor = new Charactor(1, user.getId(), "六合塚 弥生", "PSYCHO-PASS サイコパス", 
                "https://dl.dropboxusercontent.com/s/evhzmw6ckqgayq1/aine_1.png");
        createLocation(1, charactor, (float)35.664314, (float)139.862010);
        return charactor;
    }
    public static Charactor getCharactor2(User user) {
        Charactor charactor = new Charactor(2, user.getId(), "赤司 征十郎", "黒子のバスケ", 
                "https://dl.dropboxusercontent.com/s/412w2mf96350ef0/aine_2.png");
        createLocation(2, charactor, (float)35.666475, (float)139.859607);
        return charactor;
    }
    public static Charactor getCharactor3(User user) {
        Charactor charactor = new Charactor(3, user.getId(), "名瀬 美月", "境界の彼方", 
                "https://dl.dropboxusercontent.com/s/57a5yuhy45ltmk2/aine_3.png");
        createLocation(3, charactor, (float)35.665429, (float)139.872310);
        return charactor;
    }
    public static Charactor getCharactor4(User user) {
        Charactor charactor = new Charactor(4, user.getId(), "園田 海未", "ラブライブ", 
                "https://dl.dropboxusercontent.com/s/gd9gvct1l1tqjc0/aine_4.png");
        createLocation(4, charactor, (float)35.657480, (float)139.862010);
        return charactor;
    }
    public static Charactor getCharactor5(User user) {
        Charactor charactor = new Charactor(5, user.getId(), "鬼龍院 皐月", "キルラキル", 
                "https://dl.dropboxusercontent.com/s/r4nrzsp1ifi1c8q/aine_5.png");
        createLocation(5, charactor, (float)35.656085, (float)139.825274);
        return charactor;
    }
    public static Charactor getCharactor6(User user) {
        Charactor charactor = new Charactor(6, user.getId(), "ミカサ・アッカーマン", "進撃の巨人", 
                "https://dl.dropboxusercontent.com/s/9385nvtv6zyu2ol/aine_6.png");
        createLocation(6, charactor, (float)35.676446, (float)139.859263);
        return charactor;
    }
    public static Charactor getCharactor7(User user) {
        Charactor charactor = new Charactor(7, user.getId(), "セーラー・プルート", "セーラームーン", 
                "https://dl.dropboxusercontent.com/s/vxectzyhtbcxaga/aine_7.png");
        createLocation(7, charactor, (float)35.670171, (float)139.864070);
        return charactor;
    }
    public static Charactor getCharactor8(User user) {
        Charactor charactor = new Charactor(8, user.getId(), "初音ミク", "初音ミク", 
                "https://dl.dropboxusercontent.com/s/ayr8jqljg6hiaks/aine_8.png");
        createLocation(8, charactor, (float)35.659711, (float)139.859778);
        return charactor;
    }

    private static void createLocation(int id, Charactor charactor, float lat, float lon) {
        CharactorLocation location = new CharactorLocation(id, charactor.getId(), lat, lon);
        charactor.setCharactorLocation(location);
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

    public static List<CharactorComment> getComments(Charactor charactor) {
        List<CharactorComment> comments = new ArrayList<CharactorComment>();
        comments.add(getComment(1, "こんにちは！初めまして！", charactor));
        comments.add(getComment(2, "こんにちは！", charactor));
        comments.add(getComment(3, "こんにちは！初めまして！ありがとう！さようなら！", charactor));
        comments.add(getComment(4, "こんにちは！初めまして！", charactor));
        comments.add(getComment(5, "こんにちは！", charactor));
        comments.add(getComment(6, "こんにちは！初めまして！ありがとう！さようなら！", charactor));
        comments.add(getComment(7, "こんにちは！初めまして！", charactor));
        comments.add(getComment(8, "こんにちは！", charactor));
        comments.add(getComment(9, "こんにちは！初めまして！ありがとう！さようなら！", charactor));

        return comments;
    }

    private static CharactorComment getComment(int id, String text, Charactor charactor) {
        User user = getUser1();
        Charactor commentCharactor = null;
        switch(id) {
        case 1:
        case 4:
        case 7:
            commentCharactor = getCharactor1(user);
            break;
        case 2:
        case 5:
        case 8:
            commentCharactor = getCharactor2(user);
            break;
        case 3:
        case 6:
        case 9:
            commentCharactor = getCharactor3(user);
            break;
        }
        return new CharactorComment(id, text, charactor, commentCharactor);
    }

    public static Charactor createCharactor(String name, String title, String localImage) {
        return new Charactor(1, getUser1().getId(), name, title, ImageUtils.convertToValidUrl(localImage));
    }

}
