package com.onyx.android.sdk.utils;

import android.util.Log;
import com.onyx.android.sdk.data.PageInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Created by zhuzeng on 2/11/16.
 */
public class TestUtils {

    static public final String source = "八年来，我研究了我的朋友歇洛克·福abcde尔摩斯的破案方法，记录了七abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,;.'十多个案例。我粗略afebzflejqoru302ur09jv地翻阅一下这些案例的记录，发现许多案例是悲剧性的，也有一些是喜剧性的，其中很大一部分仅仅是离奇古怪而已，但是却没有一例是平淡无奇的。这是因为，他做工作与其说是为了获得酬金，还不如说是出于对他那门技艺的爱好。除了显得独特或甚至于是近乎荒诞无稽的案情外，他对其它案情从来是不屑一顾，拒不参与任何侦查的。可是，在所有这些变化多端的案例中，我却回忆不起有哪一例会比萨里郡斯托克莫兰的闻名的罗伊洛特家族①那一例更具有异乎寻常的特色了。现在谈论的这件事，发生在我和福尔摩斯交往的早期。那时，我们都是单身汉，在贝克街合住一套寓所。本来我早就可以把这件事记录下来，但是，当时我曾作出严守秘密的保证，直至上月，由于我为之作出过保证的那位女士不幸过早地逝世，方始解除了这种约束。现在，大概是使真相大白于天下的时候了，因为我确实知道，外界对于格里姆斯比·罗伊洛特医生之死众说纷纭，广泛流传着各种谣言。这些谣言使得这桩事情变得比实际情况更加骇人听闻。①英格兰东南部一郡。——译者注事情发生在一八八三年四月初的时候。一天早上，我一觉醒来，发现歇洛克·福尔摩斯穿得整整齐齐在我的床边。一般来说，他是一个爱睡懒觉的人，而壁炉架上的时钟，才刚七点一刻，我有些诧异地朝他眨了眨眼睛，心里还有点不乐意，因为我自己的生活习惯是很有规律的。“对不起，把你叫醒了，华生，\"他说，“但是，你我今天早上都命该如此，先是赫德森太太被敲门声吵醒，接着她报复似地来吵醒我，现在是我来把你叫醒。”“那么，什么事——失火了吗？”“不，是一位委托人。好象是一位年轻的女士来临，她情绪相当激动，坚持非要见我不可。现在她正在起居室里等候。你瞧，如果有些年轻的女士这么一清早就徘徊于这个大都市，甚至把还在梦乡的人从床上吵认为那必定是一件紧急的事情，她们不得不找人商量。假如这件事将是一件有趣的案子，那么，我肯定你一定希望从一开始就能有所了解。我认为无论如何应该把你叫醒，给予你这个机会。”“我的老兄，那我是无论如何也不肯失掉这个机会的。”我最大的乐趣就是观察福尔摩斯进行专业性的调查工作，欣赏他迅速地做出推论，他推论之敏捷，犹如是单凭直觉而做出的，但却总是建立在逻辑的基础之上。他就是依靠这些解决了委托给他的疑难问题。我匆匆地穿上衣服，几分钟后就准备就绪，随同我的朋友来到楼下的起居室。一位女士端坐窗前，她身穿黑色衣服，蒙着厚厚的面。她在我们走进房间时站起身来。“早上好，小姐，\"福尔摩斯愉快地说道，“我的名字是歇洛克·福尔摩斯。这位是我的挚友和伙伴华生医生。在他面前，你可以象在我面前一样地谈话，不必顾虑。哈！赫德森太太想得很周到，我很高兴看到她已经烧旺了壁炉。请凑近炉火坐坐，我叫人给你端一杯热咖啡，我看你在发抖。”“我不是因为觉得冷才发抖的，\"那个女人低声地说，同时，她按照福尔摩斯的请求换了个座位。“那么，是为什么呢？”";

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    static public boolean compareList(final List<PageInfo> a, final List<PageInfo> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for(PageInfo pageInfo : a) {
            if (!b.contains(pageInfo)) {
                return false;
            }
        }
        for(PageInfo pageInfo : b) {
            if (!a.contains(pageInfo)) {
                return false;
            }
        }
        return true;
    }

    static public boolean compareFloatWhole(float a, float b) {
        if (Math.abs(a - b) < 0.001f) {
            return true;
        } else {
            Log.e("Shit", "not equal");
            return false;
        }
    }

    static public final String randString() {
        int count = TestUtils.randInt(8, 30);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            int index = TestUtils.randInt(0, source.length() - 1);
            stringBuilder.append(source.charAt(index));
        }
        return stringBuilder.toString();
    }

    public static String randomEmail() {
        String email = UUID.randomUUID().toString().replace("-", "") + "@" + "onyx-international.com";
        while (Character.isDigit(email.charAt(0))) {
            email = email.substring(1);
        }
        return email;
    }


    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        }
    }

    public static Set<String> defaultContentTypes() {
        Set<String> defaultTypes = null;
        if (defaultTypes == null) {
            defaultTypes = new HashSet<String>();
            defaultTypes.add("epub");
            defaultTypes.add("pdf");
            defaultTypes.add("mobi");
            defaultTypes.add("prc");
            defaultTypes.add("rtf");
            defaultTypes.add("doc");
            defaultTypes.add("fb2");
            defaultTypes.add("txt");
            defaultTypes.add("docx");
            defaultTypes.add("chm");
            defaultTypes.add("djvu");
            defaultTypes.add("azw3");
            defaultTypes.add("zip");
        }
        return defaultTypes;
    }


    public static String randomType() {
        List<String> list = new ArrayList<String>();
        list.addAll(defaultContentTypes());
        int index = TestUtils.randInt(0, list.size() - 1);
        return list.get(index);
    }

    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static File generateRandomFolder(final String parent) {
        File dir = new File(parent);
        dir.mkdirs();

        File childFolder = new File(parent, generateUniqueId());
        childFolder.mkdirs();
        return childFolder;
    }

    public static File generateRandomFile(final String parent, boolean hasExtension) {
        File dir = new File(parent);
        dir.mkdirs();

        final String ext;
        if (hasExtension) {
            ext = "." + randomType();
        } else {
            ext = "";
        }
        File file = new File(parent, UUID.randomUUID().toString() + ext);
        StringBuilder builder = new StringBuilder();
        int limit = TestUtils.randInt(100, 1024);
        for (int i = 0; i < limit; ++i) {
            builder.append(UUID.randomUUID().toString());
        }
        FileUtils.saveContentToFile(builder.toString(), file);
        return file;
    }

    public static List<String> randomStringList() {
        int value = TestUtils.randInt(1, 5);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < value; ++i) {
            list.add(UUID.randomUUID().toString());
        }
        return list;
    }

    public static void deleteRecursive(final String path) {
        File file = new File(path);

        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) {
            }
        }
    }


}
